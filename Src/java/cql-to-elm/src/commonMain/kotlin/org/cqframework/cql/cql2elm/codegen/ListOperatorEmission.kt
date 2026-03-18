package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.model.ListType
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
    var result = operandElm

    // For Flatten with heterogeneous list (mixed List<T> and T elements),
    // wrap in implicit Query that casts each element to List<T>
    if (expression.listTransformKind == ListTransformKind.FLATTEN) {
        val flattenListType = detectHeterogeneousFlatten(expression.operand)
        if (flattenListType != null) {
            val queryWrapped = wrapFlattenHeterogeneous(result, flattenListType)
            return Flatten().apply { operand = queryWrapped }
        }
    }

    return when (expression.listTransformKind) {
        ListTransformKind.DISTINCT -> Distinct().apply { operand = result }
        ListTransformKind.FLATTEN -> Flatten().apply { operand = result }
    }
}

/**
 * Detect if a flatten operand is a heterogeneous list (contains both List<T> and T elements).
 * Returns the List<T> type to cast to, or null if not heterogeneous.
 */
private fun EmissionContext.detectHeterogeneousFlatten(
    operand: org.hl7.cql.ast.Expression
): ListType? {
    // Check if the operand is a list literal with mixed element types
    if (operand !is org.hl7.cql.ast.LiteralExpression) return null
    val literal = operand.literal
    if (literal !is org.hl7.cql.ast.ListLiteral) return null

    val elemTypes = literal.elements.mapNotNull { semanticModel[it] }
    if (elemTypes.isEmpty()) return null

    val hasListType = elemTypes.any { it is ListType }
    val hasNonListType = elemTypes.any { it !is ListType }

    if (hasListType && hasNonListType) {
        // Find the list element type
        val listTypes = elemTypes.filterIsInstance<ListType>()
        val elementType = listTypes.first().elementType
        return ListType(elementType)
    }
    return null
}

/**
 * Wrap a heterogeneous list in an implicit Query that casts each element to the target list type.
 * Produces: Query(source=[X from list], return=As(X, targetListType))
 */
private fun EmissionContext.wrapFlattenHeterogeneous(
    listExpr: ElmExpression,
    targetListType: ListType,
): ElmExpression {
    return org.hl7.elm.r1.Query().apply {
        source =
            mutableListOf(
                org.hl7.elm.r1.AliasedQuerySource().apply {
                    alias = "X"
                    expression = listExpr
                }
            )
        `let` = mutableListOf()
        relationship = mutableListOf()
        `return` =
            org.hl7.elm.r1.ReturnClause().apply {
                distinct = false
                expression =
                    org.hl7.elm.r1.As().apply {
                        operand = org.hl7.elm.r1.AliasRef().apply { name = "X" }
                        asTypeSpecifier =
                            operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(targetListType)
                    }
            }
    }
}
