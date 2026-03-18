package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.analysis.OperatorNames
import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.BooleanTestExpression
import org.hl7.cql.ast.BooleanTestKind
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
import org.hl7.elm.r1.IsFalse
import org.hl7.elm.r1.IsNull
import org.hl7.elm.r1.IsTrue
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
import org.hl7.elm.r1.TruncatedDivide
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.Xor

/** Table-driven map from operator name to ELM binary expression constructor. */
private val binaryConstructors: Map<String, () -> BinaryExpression> =
    mapOf(
        "Add" to { Add() },
        "Subtract" to { Subtract() },
        "Multiply" to { Multiply() },
        "Divide" to { Divide() },
        "TruncatedDivide" to { TruncatedDivide() },
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
    expression: OperatorBinaryExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val op = expression.operator

    // Handle Concatenate (&): Coalesce wrapping is inserted by ConversionInserter at the AST
    // level, so operands are already coalesced. Just emit as Concatenate.
    if (op == BinaryOperator.CONCAT) {
        return Concatenate().apply { operand = mutableListOf(leftElm, rightElm) }
    }

    // Set operators (union/intersect/except) emit as NaryExpression, not BinaryExpression
    if (
        op == BinaryOperator.UNION || op == BinaryOperator.INTERSECT || op == BinaryOperator.EXCEPT
    ) {
        return emitSetOperator(expression, leftElm, rightElm)
    }

    // For NotEqual and NotEquivalent, we emit Not(Equal/Equivalent(...))
    if (op == BinaryOperator.NOT_EQUALS) {
        return emitNotWrapper(expression, "Equal", leftElm, rightElm)
    }
    if (op == BinaryOperator.NOT_EQUIVALENT) {
        return emitNotWrapper(expression, "Equivalent", leftElm, rightElm)
    }

    val systemOpName =
        OperatorNames.binaryOperatorToSystemName(op)
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Binary operator '${op.name}' is not yet supported."
            )

    // Add→Concatenate rewrite is handled by ConversionInserter (rewrites ADD to CONCAT
    // when operator resolution determines string addition). No semantic logic here.
    val operands = mutableListOf(leftElm, rightElm)
    return createBinaryElm(systemOpName, operands)
}

internal fun EmissionContext.emitNotWrapper(
    expression: OperatorBinaryExpression,
    innerOpName: String,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val operands = mutableListOf(leftElm, rightElm)

    // Look up resolution to decorate the inner expression with the Boolean result type
    val resolution = lookupResolution(expression)

    val inner = createBinaryElm(innerOpName, operands)
    // Decorate the inner expression with the Boolean result type from the resolution
    if (resolution != null && resolution.operator.resultType != null) {
        decorate(inner, resolution.operator.resultType!!)
    }
    return Not().apply { operand = inner }
}

internal fun EmissionContext.emitUnaryOperator(
    expression: OperatorUnaryExpression,
    operandElm: ElmExpression,
): ElmExpression {
    val op = expression.operator

    // Positive is identity - just return the operand
    if (op == UnaryOperator.POSITIVE) {
        return operandElm
    }

    val systemOpName =
        OperatorNames.unaryOperatorToSystemName(op)
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Unary operator '${op.name}' is not yet supported."
            )

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

/**
 * Emit a boolean test expression: `x is null`, `x is true`, `x is false` (and their negated
 * variants `x is not null`, etc.). These emit as IsNull/IsTrue/IsFalse unary operators, wrapped in
 * Not for negated variants.
 */
internal fun EmissionContext.emitBooleanTest(
    expression: BooleanTestExpression,
    operandElm: ElmExpression,
): ElmExpression {
    val innerElm: ElmExpression =
        when (expression.kind) {
            BooleanTestKind.IS_NULL -> IsNull().apply { operand = operandElm }
            BooleanTestKind.IS_TRUE -> IsTrue().apply { operand = operandElm }
            BooleanTestKind.IS_FALSE -> IsFalse().apply { operand = operandElm }
        }

    // Set result type on the inner expression from the resolution
    val resolution = lookupResolution(expression)
    if (resolution != null && resolution.operator.resultType != null) {
        decorate(innerElm, resolution.operator.resultType!!)
    }

    // For `is not null`, `is not true`, `is not false`, wrap in Not
    return if (expression.negated) {
        Not().apply { operand = innerElm }
    } else {
        innerElm
    }
}
