package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.ListTransformExpression
import org.hl7.cql.ast.ListTransformKind
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.IntervalType
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
internal fun EmissionContext.emitSetOperator(
    expression: OperatorBinaryExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    var left = leftElm
    var right = rightElm

    // Apply list-demotion conversions (List<Any> → List<T> via implicit Query with As cast).
    // These are deferred from ConversionInserter because they are only needed here (set operators),
    // not for other binary or function contexts where the cast list conversion is a no-op.
    val resolution = lookupResolution(expression)
    if (resolution != null && resolution.hasConversions()) {
        resolution.conversions.forEachIndexed { index, conversion ->
            if (
                conversion != null &&
                    conversion.isListConversion &&
                    conversion.conversion != null &&
                    conversion.conversion!!.isCast
            ) {
                val target = if (index == 0) left else right
                val wrapped = wrapListConversion(target, conversion.conversion!!)
                if (index == 0) left = wrapped else right = wrapped
            }
        }
    }

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
                val sortedTypes = listOf(leftElem, rightElem).distinct().sortedBy { it.toString() }
                val choiceElem = ChoiceType(sortedTypes)
                val choiceListType = ListType(choiceElem)
                left = wrapAsListChoice(left, choiceListType)
                right = wrapAsListChoice(right, choiceListType)
            }
        }
    }

    val operands = mutableListOf(left, right)
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
