package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.Union

/**
 * Emit a set operator (union/intersect/except) as the corresponding ELM NaryExpression. Children
 * are pre-folded.
 */
/**
 * Emit set operators. Purely mechanical — all conversions and choice type wrapping handled by
 * ConversionInserter via the INFER→CONVERT→CHECK loop.
 */
internal fun EmissionContext.emitSetOperator(
    expression: OperatorBinaryExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val operands = mutableListOf(leftElm, rightElm)
    return when (expression.operator) {
        BinaryOperator.UNION -> Union().apply { operand = operands }
        BinaryOperator.INTERSECT -> Intersect().apply { operand = operands }
        BinaryOperator.EXCEPT -> Except().apply { operand = operands }
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Set operator '${expression.operator.name}' is not supported."
            )
    }
}

/**
 * Emit a [ListTransformExpression] (distinct/flatten) as the corresponding ELM unary node. Operand
 * is pre-folded.
 *
 * Null-As wrapping is handled by ConversionInserter before emission.
 */
internal fun EmissionContext.emitListTransform(
    expression: ListTransformExpression,
    operandElm: ElmExpression,
): ElmExpression {
    // Heterogeneous flatten detection moved to ConversionPlanner — non-list elements
    // are wrapped via ImplicitCast(List<T>) conversions on the list literal's elements.
    return when (expression.listTransformKind) {
        ListTransformKind.DISTINCT -> Distinct().apply { operand = operandElm }
        ListTransformKind.FLATTEN -> Flatten().apply { operand = operandElm }
    }
}

// detectHeterogeneousFlatten and wrapFlattenHeterogeneous deleted — moved to
// ConversionPlanner.recordHeterogeneousFlattenConversions as ImplicitCast conversions.
