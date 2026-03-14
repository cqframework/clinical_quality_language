package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.TypeResolver
import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.OperatorUnaryExpression
import org.hl7.cql.ast.UnaryOperator
import org.hl7.elm.r1.Add
import org.hl7.elm.r1.And
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.Concatenate
import org.hl7.elm.r1.Divide
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Equivalent
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Greater
import org.hl7.elm.r1.GreaterOrEqual
import org.hl7.elm.r1.Implies
import org.hl7.elm.r1.Less
import org.hl7.elm.r1.LessOrEqual
import org.hl7.elm.r1.Modulo
import org.hl7.elm.r1.Multiply
import org.hl7.elm.r1.Negate
import org.hl7.elm.r1.Not
import org.hl7.elm.r1.Or
import org.hl7.elm.r1.Power
import org.hl7.elm.r1.Predecessor
import org.hl7.elm.r1.Subtract
import org.hl7.elm.r1.Successor
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.Xor

/** Table-driven map from operator name to ELM binary expression constructor. */
private val binaryConstructors: Map<String, () -> BinaryExpression> =
    mapOf(
        "Add" to { Add() },
        "Subtract" to { Subtract() },
        "Multiply" to { Multiply() },
        "Divide" to { Divide() },
        "Modulo" to { Modulo() },
        "Power" to { Power() },
        "Equal" to { Equal() },
        "Equivalent" to { Equivalent() },
        "Less" to { Less() },
        "LessOrEqual" to { LessOrEqual() },
        "Greater" to { Greater() },
        "GreaterOrEqual" to { GreaterOrEqual() },
        "And" to { And() },
        "Or" to { Or() },
        "Xor" to { Xor() },
        "Implies" to { Implies() },
    )

/** Table-driven map from operator name to ELM unary expression constructor. */
private val unaryConstructors: Map<String, () -> UnaryExpression> =
    mapOf(
        "Negate" to { Negate() },
        "Not" to { Not() },
        "Successor" to { Successor() },
        "Predecessor" to { Predecessor() },
    )

@Suppress("CyclomaticComplexMethod", "ReturnCount", "NestedBlockDepth")
internal fun EmissionContext.emitBinaryOperator(
    expression: OperatorBinaryExpression
): ElmExpression {
    val op = expression.operator

    // Handle Concatenate (&) specially - it wraps each operand in Coalesce(operand, '')
    if (op == BinaryOperator.CONCAT) {
        return emitConcatenate(expression)
    }

    // For NotEqual and NotEquivalent, we emit Not(Equal/Equivalent(...))
    if (op == BinaryOperator.NOT_EQUALS) {
        return emitNotWrapper(expression, "Equal")
    }
    if (op == BinaryOperator.NOT_EQUIVALENT) {
        return emitNotWrapper(expression, "Equivalent")
    }

    val systemOpName =
        TypeResolver.binaryOperatorToSystemName(op)
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Binary operator '${op.name}' is not yet supported."
            )

    var leftElm = emitExpression(expression.left)
    var rightElm = emitExpression(expression.right)

    // Use the pre-computed operator resolution from TypeTable
    val resolution = lookupResolution(expression)
    if (resolution != null) {
        applyConversions(resolution) { index, convName ->
            when (index) {
                0 -> leftElm = wrapConversion(leftElm, convName)
                1 -> rightElm = wrapConversion(rightElm, convName)
            }
        }

        // Special case: Add on strings becomes Concatenate
        if (
            systemOpName == "Add" &&
                resolution.operator.resultType == operatorRegistry.type("String")
        ) {
            return Concatenate().apply { operand = mutableListOf(leftElm, rightElm) }
        }
    }

    return createBinaryElm(systemOpName, mutableListOf(leftElm, rightElm))
}

internal fun EmissionContext.emitNotWrapper(
    expression: OperatorBinaryExpression,
    innerOpName: String,
): ElmExpression {
    var leftElm = emitExpression(expression.left)
    var rightElm = emitExpression(expression.right)

    // Use the pre-computed operator resolution from TypeTable
    val resolution = lookupResolution(expression)
    if (resolution != null) {
        applyConversions(resolution) { index, convName ->
            when (index) {
                0 -> leftElm = wrapConversion(leftElm, convName)
                1 -> rightElm = wrapConversion(rightElm, convName)
            }
        }
    }

    val inner = createBinaryElm(innerOpName, mutableListOf(leftElm, rightElm))
    // Decorate the inner expression with the Boolean result type from the resolution
    if (resolution != null && resolution.operator.resultType != null) {
        decorate(inner, resolution.operator.resultType!!)
    }
    return Not().apply { operand = inner }
}

internal fun EmissionContext.emitConcatenate(expression: OperatorBinaryExpression): ElmExpression {
    val leftElm = emitExpression(expression.left)
    val rightElm = emitExpression(expression.right)

    // Legacy translator wraps each operand in Coalesce(operand, '') for & operator
    return Concatenate().apply {
        operand = mutableListOf(wrapCoalesce(leftElm), wrapCoalesce(rightElm))
    }
}

internal fun EmissionContext.emitUnaryOperator(expression: OperatorUnaryExpression): ElmExpression {
    val op = expression.operator

    // Positive is identity - just return the operand
    if (op == UnaryOperator.POSITIVE) {
        return emitExpression(expression.operand)
    }

    val systemOpName =
        TypeResolver.unaryOperatorToSystemName(op)
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Unary operator '${op.name}' is not yet supported."
            )

    var operandElm = emitExpression(expression.operand)

    // Use the pre-computed operator resolution from TypeTable
    val resolution = lookupResolution(expression)
    if (resolution != null) {
        applyConversions(resolution) { _, convName ->
            operandElm = wrapConversion(operandElm, convName)
        }
    }

    return createUnaryElm(systemOpName, operandElm)
}

/** Create the appropriate ELM binary expression node using the table-driven constructor map. */
internal fun EmissionContext.createBinaryElm(
    operatorName: String,
    operands: MutableList<ElmExpression>,
): ElmExpression {
    val constructor =
        binaryConstructors[operatorName]
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Binary operator '$operatorName' ELM emission is not yet supported."
            )
    return constructor().apply { operand = operands }
}

/** Create the appropriate ELM unary expression node using the table-driven constructor map. */
internal fun EmissionContext.createUnaryElm(
    operatorName: String,
    operand: ElmExpression,
): ElmExpression {
    val constructor =
        unaryConstructors[operatorName]
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Unary operator '$operatorName' ELM emission is not yet supported."
            )
    return constructor().apply { this.operand = operand }
}
