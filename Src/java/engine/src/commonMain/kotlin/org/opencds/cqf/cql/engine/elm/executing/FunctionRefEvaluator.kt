package org.opencds.cqf.cql.engine.elm.executing

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.jvm.JvmStatic
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.qnamesEqual
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.typeSpecifiersEqual
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.NamedTypeValue
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.anyTypeName

object FunctionRefEvaluator {
    private val logger = KotlinLogging.logger("FunctionRefEvaluator")

    fun internalEvaluate(
        functionRef: FunctionRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        val arguments: ArrayList<Value?> = ArrayList(functionRef!!.operand.size)
        for (operand in functionRef.operand) {
            arguments.add(visitor.visitExpression(operand, state))
        }

        val enteredLibrary = state!!.enterLibrary(functionRef.libraryName)
        try {
            val functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments)

            return evaluateFunctionDef(functionDef, state, visitor, arguments)
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }

    fun evaluateFunctionDef(
        functionDef: FunctionDef,
        state: State,
        visitor: ElmLibraryVisitor<Value?, State?>,
        arguments: MutableList<Value?>,
    ): Value? {
        if (true == functionDef.isExternal()) {
            return state.environment
                .getExternalFunctionProvider(state.getCurrentLibrary()!!.identifier)
                .evaluate(functionDef.name, arguments)
        } else {
            // Establish activation frame with the function
            // definition being evaluated.
            state.pushActivationFrame(functionDef, functionDef.context!!)
            try {
                for (i in arguments.indices) {
                    state.push(Variable(functionDef.operand[i].name!!).withValue(arguments[i]))
                }
                val result = visitor.visitExpression(functionDef.expression!!, state)
                state.storeIntermediateResultForTracing(result)
                return result
            } finally {
                state.popActivationFrame()
            }
        }
    }

    internal fun resolveOrCacheFunctionDef(
        state: State?,
        functionRef: FunctionRef,
        arguments: kotlin.collections.List<Value?>,
    ): FunctionDef {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        var eligibleForCaching = false
        if (!functionRef.signature.isEmpty() || arguments.isEmpty()) {
            eligibleForCaching = true
            if (state!!.cache.functionCache.containsKey(functionRef)) {
                return state.cache.functionCache[functionRef]!!
            }
        }

        val functionDef = resolveFunctionRef(state, functionRef, arguments)

        if (eligibleForCaching) {
            state!!.cache.functionCache[functionRef] = functionDef
        }

        return functionDef
    }

    internal fun resolveFunctionRef(
        state: State?,
        functionRef: FunctionRef,
        arguments: kotlin.collections.List<Value?>,
    ): FunctionDef {
        val name = functionRef.name
        val signature = functionRef.signature

        val functionDefs = resolveFunctionRef(state, name, arguments, signature)

        return pickFunctionDef(state, name, arguments, signature, functionDefs)
    }

    fun resolveFunctionRef(
        state: State?,
        name: String?,
        arguments: kotlin.collections.List<Value?>,
        signature: kotlin.collections.List<TypeSpecifier>,
    ): kotlin.collections.List<FunctionDef> {
        val namedDefs = Libraries.getFunctionDefs(name, state!!.getCurrentLibrary()!!)

        // If the function ref includes a signature, use the signature to find the matching function
        // defs
        if (!signature.isEmpty()) {
            return namedDefs.filter { x -> functionDefOperandsSignatureEqual(x, signature) }
        }

        logger.debug {
            "Using runtime function resolution for '$name'. It's recommended to always include signatures in ELM"
        }

        return namedDefs.filter { x -> matchesTypes(x, arguments, state) }
    }

    fun functionDefOperandsSignatureEqual(
        functionDef: FunctionDef,
        signature: kotlin.collections.List<TypeSpecifier>,
    ): Boolean {
        val operands = functionDef.operand

        // Check if the number of operands match and if the type specifiers match
        return operands.size == signature.size &&
            (0 until operands.size).all { i ->
                operandDefTypeSpecifierEqual(operands[i], signature[i])
            }
    }

    @JvmStatic
    fun operandDefTypeSpecifierEqual(
        operandDef: OperandDef,
        typeSpecifier: TypeSpecifier?,
    ): Boolean {
        // An operand def can have an operandTypeSpecifier or operandType

        val operandDefOperandTypeSpecifier = operandDef.operandTypeSpecifier
        return if (operandDefOperandTypeSpecifier != null) {
            typeSpecifiersEqual(operandDefOperandTypeSpecifier, typeSpecifier)
        } else if (typeSpecifier is NamedTypeSpecifier) {
            qnamesEqual(operandDef.operandType, typeSpecifier.name)
        } else false
    }

    fun pickFunctionDef(
        state: State?,
        name: String?,
        arguments: kotlin.collections.List<Value?>,
        signature: kotlin.collections.List<TypeSpecifier>,
        functionDefs: kotlin.collections.List<FunctionDef>,
    ): FunctionDef {
        val types =
            if (signature.isEmpty()) {
                arguments.joinToString(", ") { it?.typeAsString ?: "null" }
            } else {
                signature.joinToString(", ")
            }

        if (functionDefs.isEmpty()) {
            throw CqlException(
                "Could not resolve call to operator '${name}(${types})' in library '${state!!.getCurrentLibrary()!!.identifier!!.id}'."
            )
        }

        if (functionDefs.size == 1) {
            // Normal case
            return functionDefs[0]
        }

        throw CqlException(
            "Ambiguous call to operator '${name}(${types})' in library '${state!!.getCurrentLibrary()!!.identifier!!.id}'."
        )
    }

    fun resolveOperandType(operandDef: OperandDef): TypeSpecifier {
        return if (operandDef.operandTypeSpecifier != null) {
            operandDef.operandTypeSpecifier!!
        } else {
            NamedTypeSpecifier().withName(operandDef.operandType)
        }
    }

    fun matchesTypes(
        functionDef: FunctionDef,
        arguments: kotlin.collections.List<Value?>,
        state: State?,
    ): Boolean {
        val operands = functionDef.operand

        // if argument length is mismatched, don't compare
        if (arguments.size != operands.size) {
            return false
        }

        // Types must not be incompatible
        return arguments.zip(operands).all { (argument, operand) ->
            isCompatible(argument, resolveOperandType(operand), state) != false
        }
    }

    /**
     * Returns true if the value's type is compatible with the specified type (so e.g. the value can
     * be passed to a function expecting the specified type). Returns null if we cannot determine
     * compatibility. This is not the same as is-checking (see [IsEvaluator.`is`]).
     */
    fun isCompatible(value: Value?, type: TypeSpecifier, state: State?): Boolean? {
        // System.Any is a supertype of all types
        if (type is NamedTypeSpecifier && type.name == anyTypeName) {
            return true
        }

        // null is compatible with all types
        if (value == null) {
            return true
        }

        when (type) {
            is NamedTypeSpecifier -> {
                if (value is NamedTypeValue) {
                    val valueNamedType = value.type

                    if (valueNamedType == type.name) {
                        // Types are the same
                        return true
                    }

                    val provider =
                        state!!
                            .environment
                            .resolveDataProviderByModelUriOrNull(valueNamedType.getNamespaceURI())

                    if (provider == null) {
                        // Cannot determine compatibility
                        return null
                    }

                    return provider.`is`(valueNamedType.getLocalPart(), type.name!!)
                }

                // value is not an instance of a named type
                return false
            }
            is ListTypeSpecifier -> {
                if (value is List) {
                    if (value.any()) {
                        for (item in value) {
                            val result = isCompatible(item, type.elementType!!, state)
                            if (result == null) {
                                // Found an element for which we cannot determine compatibility
                                return null
                            }
                            if (result == false) {
                                // Found an element that is not compatible
                                return false
                            }
                        }
                        return true
                    }

                    // An empty list is compatible with all list types
                    return true
                }

                // Must be an Iterable to be compatible with a list type
                return false
            }
            is IntervalTypeSpecifier -> {
                if (value is Interval) {
                    val lowResult = isCompatible(value.low, type.pointType!!, state)
                    if (lowResult == false) {
                        return false
                    }

                    val highResult = isCompatible(value.high, type.pointType!!, state)
                    if (highResult == false) {
                        return false
                    }

                    if (lowResult == true || highResult == true) {
                        return true
                    }

                    return null
                }

                // Must be an interval to be compatible with an interval type
                return false
            }
            is TupleTypeSpecifier -> {
                if (value is Tuple) {
                    for (elementDefinition in type.element) {
                        if (!value.elements.containsKey(elementDefinition.name!!)) {
                            // Value is missing an element
                            return false
                        }
                        val elementValue = value.elements[elementDefinition.name!!]
                        val result =
                            isCompatible(elementValue, elementDefinition.elementType!!, state)
                        if (result == null) {
                            // Found an element for which we cannot determine compatibility
                            return null
                        }
                        if (result == false) {
                            // Found an element that is not compatible
                            return false
                        }
                    }
                    return true
                }

                // Must be a tuple to be compatible with a tuple type
                return false
            }
            is ChoiceTypeSpecifier -> {
                var foundNull = false
                for (choice in type.choice) {
                    val result = isCompatible(value, choice, state)
                    if (result == null) {
                        foundNull = true
                    }
                    if (result == true) {
                        // Found a type that is compatible
                        return true
                    }
                }
                return if (foundNull) {
                    null
                } else {
                    false
                }
            }
            else -> {
                throw IllegalArgumentException("Unexpected type specifier: $type.")
            }
        }
    }
}
