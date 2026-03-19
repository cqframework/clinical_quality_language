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

/**
 * Unified table-driven map from operator name to ELM expression factory. Covers both
 * [BinaryExpression] and [org.hl7.elm.r1.NaryExpression] subtypes — both accept an operand list.
 * Each factory receives its operands and returns a fully wired ELM node.
 */
private val operandListConstructors: Map<String, (MutableList<ElmExpression>) -> ElmExpression> =
    mapOf(
        // Binary operators
        "Add" to { ops -> Add().apply { operand = ops } },
        "Subtract" to { ops -> Subtract().apply { operand = ops } },
        "Multiply" to { ops -> Multiply().apply { operand = ops } },
        "Divide" to { ops -> Divide().apply { operand = ops } },
        "TruncatedDivide" to { ops -> TruncatedDivide().apply { operand = ops } },
        "Modulo" to { ops -> Modulo().apply { operand = ops } },
        "Power" to { ops -> Power().apply { operand = ops } },
        "Equal" to { ops -> Equal().apply { operand = ops } },
        "Equivalent" to { ops -> Equivalent().apply { operand = ops } },
        "Less" to { ops -> Less().apply { operand = ops } },
        "LessOrEqual" to { ops -> LessOrEqual().apply { operand = ops } },
        "Greater" to { ops -> Greater().apply { operand = ops } },
        "GreaterOrEqual" to { ops -> GreaterOrEqual().apply { operand = ops } },
        "And" to { ops -> And().apply { operand = ops } },
        "Or" to { ops -> Or().apply { operand = ops } },
        "Xor" to { ops -> Xor().apply { operand = ops } },
        "Implies" to { ops -> Implies().apply { operand = ops } },
        // Nary operators (same operand-list interface)
        "Concatenate" to { ops -> Concatenate().apply { operand = ops } },
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

    // Handle Concatenate (&): wrap operands in Coalesce(x, '') for null-safety
    if (op == BinaryOperator.CONCAT) {
        return createOperandListElm(
            "Concatenate",
            mutableListOf(emitCoalesceWrap(leftElm), emitCoalesceWrap(rightElm)),
        )
    }

    // Add on strings → emit as Concatenate (structural lowering, not a type conversion)
    if (op == BinaryOperator.ADD) {
        val resolution = lookupResolution(expression)
        if (resolution?.operator?.resultType?.toString() == "System.String") {
            return createOperandListElm("Concatenate", mutableListOf(leftElm, rightElm))
        }
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

    val operands = mutableListOf(leftElm, rightElm)
    return createOperandListElm(systemOpName, operands)
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

    val inner = createOperandListElm(innerOpName, operands)
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

/** Create the appropriate ELM expression node using the unified [operandListConstructors] table. */
internal fun createOperandListElm(
    operatorName: String,
    operands: MutableList<ElmExpression>,
): ElmExpression {
    val constructor =
        operandListConstructors[operatorName]
            ?: throw ElmEmitter.UnsupportedNodeException(
                "Operator '$operatorName' ELM emission is not yet supported."
            )
    return constructor(operands)
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
