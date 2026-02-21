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

object FunctionRefEvaluator {
    private val logger = KotlinLogging.logger("FunctionRefEvaluator")

    fun internalEvaluate(
        functionRef: FunctionRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val arguments: ArrayList<Any?> = ArrayList(functionRef!!.operand.size)
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
        visitor: ElmLibraryVisitor<Any?, State?>,
        arguments: MutableList<Any?>,
    ): Any? {
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
        arguments: kotlin.collections.List<Any?>,
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
        arguments: kotlin.collections.List<Any?>,
    ): FunctionDef {
        val name = functionRef.name
        val signature = functionRef.signature

        val functionDefs = resolveFunctionRef(state, name, arguments, signature)

        return pickFunctionDef(state, name, arguments, signature, functionDefs)
    }

    fun resolveFunctionRef(
        state: State?,
        name: String?,
        arguments: kotlin.collections.List<Any?>,
        signature: kotlin.collections.List<TypeSpecifier>,
    ): kotlin.collections.List<FunctionDef> {
        val namedDefs = Libraries.getFunctionDefs(name, state!!.getCurrentLibrary()!!)

        // If the function ref includes a signature, use the signature to find the matching function
        // defs
        if (!signature.isEmpty()) {
            return namedDefs.filter { x -> functionDefOperandsSignatureEqual(x, signature) }
        }

        logger.debug(
            "Using runtime function resolution for '{}'. It's recommended to always include signatures in ELM",
            name,
        )

        return namedDefs.filter { x -> state.environment.matchesTypes(x, arguments) }
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
        arguments: kotlin.collections.List<Any?>?,
        signature: kotlin.collections.List<TypeSpecifier>,
        functionDefs: kotlin.collections.List<FunctionDef>,
    ): FunctionDef {
        val types = signature.ifEmpty { arguments }

        if (functionDefs.isEmpty()) {
            throw CqlException(
                "Could not resolve call to operator '${name}(${typesToString(state, types)})' in library '${state!!.getCurrentLibrary()!!.identifier!!.id}'."
            )
        }

        if (functionDefs.size == 1) {
            // Normal case
            return functionDefs[0]
        }

        throw CqlException(
            "Ambiguous call to operator '${name}(${typesToString(state, types)})' in library '${state!!.getCurrentLibrary()!!.identifier!!.id}'."
        )
    }

    fun typesToString(state: State?, arguments: kotlin.collections.List<Any?>?): String {
        val argStr = StringBuilder()
        if (arguments != null) {
            arguments.forEach { a ->
                argStr.append(if (argStr.isNotEmpty()) ", " else "")
                val type = state!!.environment.resolveType(a)
                argStr.append(if (type == null) "null" else type.getName())
            }
        }

        return argStr.toString()
    }
}
