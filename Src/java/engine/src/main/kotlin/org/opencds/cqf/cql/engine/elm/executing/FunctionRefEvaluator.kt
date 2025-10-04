package org.opencds.cqf.cql.engine.elm.executing

import java.util.function.IntPredicate
import java.util.stream.IntStream
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.qnamesEqual
import org.cqframework.cql.elm.evaluating.SimpleElmEvaluator.typeSpecifiersEqual
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.Libraries
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.execution.Variable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object FunctionRefEvaluator {
    private val logger: Logger = LoggerFactory.getLogger(FunctionRefEvaluator::class.java)

    fun internalEvaluate(
        functionRef: FunctionRef?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val arguments: ArrayList<Any?> = ArrayList<Any?>(functionRef!!.operand.size)
        for (operand in functionRef.operand) {
            arguments.add(visitor.visitExpression(operand, state))
        }

        val enteredLibrary = state!!.enterLibrary(functionRef.libraryName)
        try {
            val functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments)

            if (true == functionDef.isExternal()) {
                return state.environment
                    .getExternalFunctionProvider(state.getCurrentLibrary()!!.identifier)
                    .evaluate(functionDef.name, arguments)
            } else {
                // Establish activation frame with the function
                // definition being evaluated.
                state.pushActivationFrame(functionDef, functionDef.context)
                try {
                    for (i in arguments.indices) {
                        state.push(
                            Variable(functionDef.operand.get(i).name!!).withValue(arguments.get(i))
                        )
                    }
                    return visitor.visitExpression(functionDef.expression!!, state)
                } finally {
                    state.popActivationFrame()
                }
            }
        } finally {
            state.exitLibrary(enteredLibrary)
        }
    }

    internal fun resolveOrCacheFunctionDef(
        state: State?,
        functionRef: FunctionRef,
        arguments: List<Any?>,
    ): FunctionDef {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        var eligibleForCaching = false
        if (!functionRef.signature.isEmpty() || arguments.isEmpty()) {
            eligibleForCaching = true
            if (state!!.cache.functionCache.containsKey(functionRef)) {
                return state.cache.functionCache.get(functionRef)!!
            }
        }

        val functionDef = resolveFunctionRef(state, functionRef, arguments)

        if (eligibleForCaching) {
            state!!.cache.functionCache.put(functionRef, functionDef)
        }

        return functionDef
    }

    internal fun resolveFunctionRef(
        state: State?,
        functionRef: FunctionRef,
        arguments: List<Any?>,
    ): FunctionDef {
        val name = functionRef.name
        val signature = functionRef.signature

        val functionDefs = resolveFunctionRef(state, name, arguments, signature)

        return pickFunctionDef(state, name, arguments, signature, functionDefs)
    }

    fun resolveFunctionRef(
        state: State?,
        name: String?,
        arguments: List<Any?>,
        signature: List<TypeSpecifier>,
    ): List<FunctionDef> {
        val namedDefs = Libraries.getFunctionDefs(name, state!!.getCurrentLibrary()!!)

        // If the function ref includes a signature, use the signature to find the matching function
        // defs
        if (!signature.isEmpty()) {
            return namedDefs.filter { x ->
                FunctionRefEvaluator.functionDefOperandsSignatureEqual(x!!, signature)
            }
        }

        logger.debug(
            "Using runtime function resolution for '{}'. It's recommended to always include signatures in ELM",
            name,
        )

        return namedDefs.filter { x -> state.environment.matchesTypes(x!!, arguments) }
    }

    fun functionDefOperandsSignatureEqual(
        functionDef: FunctionDef,
        signature: List<TypeSpecifier>,
    ): kotlin.Boolean {
        val operands = functionDef.operand

        // Check if the number of operands match and if the type specifiers match
        return operands.size == signature.size &&
            IntStream.range(0, operands.size)
                .allMatch(
                    IntPredicate { i: Int ->
                        FunctionRefEvaluator.operandDefTypeSpecifierEqual(
                            operands.get(i)!!,
                            signature.get(i),
                        )
                    }
                )
    }

    @JvmStatic
    fun operandDefTypeSpecifierEqual(
        operandDef: OperandDef,
        typeSpecifier: TypeSpecifier?,
    ): kotlin.Boolean {
        // An operand def can have an operandTypeSpecifier or operandType

        val operandDefOperandTypeSpecifier = operandDef.operandTypeSpecifier
        if (operandDefOperandTypeSpecifier != null) {
            return typeSpecifiersEqual(operandDefOperandTypeSpecifier, typeSpecifier)
        }

        if (typeSpecifier is NamedTypeSpecifier) {
            return qnamesEqual(operandDef.operandType, typeSpecifier.name)
        }

        return false
    }

    fun pickFunctionDef(
        state: State?,
        name: String?,
        arguments: List<Any?>?,
        signature: List<TypeSpecifier>,
        functionDefs: List<FunctionDef>,
    ): FunctionDef {
        val types = if (signature.isEmpty()) arguments else signature

        if (functionDefs.isEmpty()) {
            throw CqlException(
                String.format(
                    "Could not resolve call to operator '%s(%s)' in library '%s'.",
                    name,
                    typesToString(state, types),
                    state!!.getCurrentLibrary()!!.identifier!!.id,
                )
            )
        }

        if (functionDefs.size == 1) {
            // Normal case
            return functionDefs.get(0)
        }

        throw CqlException(
            String.format(
                "Ambiguous call to operator '%s(%s)' in library '%s'.",
                name,
                typesToString(state, types),
                state!!.getCurrentLibrary()!!.identifier!!.id,
            )
        )
    }

    fun typesToString(state: State?, arguments: List<Any?>?): String {
        val argStr = StringBuilder()
        if (arguments != null) {
            arguments.forEach { a ->
                argStr.append(if (argStr.isNotEmpty()) ", " else "")
                val type = state!!.environment.resolveType(a)
                argStr.append(if (type == null) "null" else type.typeName)
            }
        }

        return argStr.toString()
    }
}
