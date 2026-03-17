package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SimpleType
import org.hl7.elm.r1.Distinct
import org.hl7.elm.r1.Except
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.Intersect
import org.hl7.elm.r1.Union

/** Emit a set operator (union/intersect/except) as the corresponding ELM NaryExpression. */
internal fun EmissionContext.emitSetOperator(expression: OperatorBinaryExpression): ElmExpression {
    var leftElm = emitExpression(expression.left)
    var rightElm = emitExpression(expression.right)

    // For union, wrap operands in As when element types differ (choice type promotion)
    if (expression.operator == BinaryOperator.UNION) {
        val leftType = semanticModel[expression.left]
        val rightType = semanticModel[expression.right]
        if (leftType is ListType && rightType is ListType) {
            val leftElem = leftType.elementType
            val rightElem = rightType.elementType
            // Only wrap when element types are truly incompatible simple/class types
            // (not subtypes, not already choice/interval/list types)
            val simpleElements =
                leftElem !is ChoiceType &&
                    rightElem !is ChoiceType &&
                    leftElem !is IntervalType &&
                    rightElem !is IntervalType &&
                    leftElem !is ListType &&
                    rightElem !is ListType
            if (
                leftElem != rightElem &&
                    simpleElements &&
                    !leftElem.isSuperTypeOf(rightElem) &&
                    !rightElem.isSuperTypeOf(leftElem)
            ) {
                // Sort types to match legacy ordering (alphabetical by toString)
                val sortedTypes =
                    listOf(leftElem, rightElem).distinct().sortedBy { it.toString() }
                val choiceElem = ChoiceType(sortedTypes)
                val choiceListType = ListType(choiceElem)
                leftElm = wrapAsListChoice(leftElm, choiceListType)
                rightElm = wrapAsListChoice(rightElm, choiceListType)
            }
        }
    }

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

/** Wrap an expression in As(ListTypeSpecifier(ChoiceTypeSpecifier(...))). */
private fun EmissionContext.wrapAsListChoice(
    expression: ElmExpression,
    targetListType: ListType,
): ElmExpression {
    return org.hl7.elm.r1.As().apply {
        operand = expression
        asTypeSpecifier = operatorRegistry.typeBuilder.dataTypeToTypeSpecifier(targetListType)
    }
}

/** Emit a [ListTransformExpression] (distinct/flatten) as the corresponding ELM unary node. */
internal fun EmissionContext.emitListTransform(expression: ListTransformExpression): ElmExpression {
    var operandElm = emitExpression(expression.operand)

    // Null-As wrapping for null operands
    if (isNullLiteralExpr(expression.operand)) {
        val anyType = operatorRegistry.type("Any")
        operandElm =
            when (expression.listTransformKind) {
                // distinct null → As(List<Any>)
                ListTransformKind.DISTINCT -> wrapNullAs(operandElm, ListType(anyType))
                // flatten null → As(List<List<Any>>)
                ListTransformKind.FLATTEN -> wrapNullAs(operandElm, ListType(ListType(anyType)))
            }
    }

    // For Flatten with heterogeneous list (mixed List<T> and T elements),
    // wrap in implicit Query that casts each element to List<T>
    if (expression.listTransformKind == ListTransformKind.FLATTEN && !isNullLiteralExpr(expression.operand)) {
        val flattenListType = detectHeterogeneousFlatten(expression.operand)
        if (flattenListType != null) {
            val queryWrapped = wrapFlattenHeterogeneous(operandElm, flattenListType)
            return Flatten().apply { operand = queryWrapped }
        }
    }

    return when (expression.listTransformKind) {
        ListTransformKind.DISTINCT -> Distinct().apply { operand = operandElm }
        ListTransformKind.FLATTEN -> Flatten().apply { operand = operandElm }
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
