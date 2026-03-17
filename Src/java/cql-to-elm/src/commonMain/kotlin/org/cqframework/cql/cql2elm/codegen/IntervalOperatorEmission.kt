@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BeforeOrAfterIntervalPhrase
import org.hl7.cql.ast.ConcurrentIntervalPhrase
import org.hl7.cql.ast.ConcurrentQualifier
import org.hl7.cql.ast.EndsIntervalPhrase
import org.hl7.cql.ast.IncludedInIntervalPhrase
import org.hl7.cql.ast.IncludesIntervalPhrase
import org.hl7.cql.ast.IntervalBoundarySelector
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.MeetsIntervalPhrase
import org.hl7.cql.ast.OverlapsIntervalPhrase
import org.hl7.cql.ast.StartsIntervalPhrase
import org.hl7.cql.ast.TemporalRelationshipDirection
import org.hl7.cql.ast.WithinIntervalPhrase
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.Contains
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Ends
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.In
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Includes
import org.hl7.elm.r1.Meets
import org.hl7.elm.r1.MeetsAfter
import org.hl7.elm.r1.MeetsBefore
import org.hl7.elm.r1.Overlaps
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.OverlapsBefore
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.ProperIn
import org.hl7.elm.r1.ProperIncludedIn
import org.hl7.elm.r1.ProperIncludes
import org.hl7.elm.r1.SameAs
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Starts

/** Emit an [IntervalRelationExpression] by dispatching on the phrase type. */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitIntervalRelation(
    expression: IntervalRelationExpression
): ElmExpression {
    var leftElm = emitExpression(expression.left)
    var rightElm = emitExpression(expression.right)

    // Null-As wrapping for interval relation operands
    val leftIsNull = isNullLiteralExpr(expression.left)
    val rightIsNull = isNullLiteralExpr(expression.right)
    val leftType = semanticModel[expression.left]
    val rightType = semanticModel[expression.right]

    if (rightIsNull && leftType is IntervalType) {
        // Right operand is null, left is an interval → wrap null as point type
        rightElm = wrapNullAs(rightElm, leftType.pointType)
    } else if (leftIsNull && rightType is IntervalType) {
        // Left operand is null, right is an interval → wrap null as point type
        leftElm = wrapNullAs(leftElm, rightType.pointType)
    }

    // Interval<Any> expansion: when the RIGHT operand is Interval<Any> and the LEFT is
    // a concrete Interval<T>, expand the right to match. This mirrors the legacy behavior
    // where operator resolution binds T from the left operand and converts the right.
    // (When the LEFT is Interval<Any>, the legacy uses T=Any and no conversion occurs.)
    if (
        leftType is IntervalType &&
            rightType is IntervalType &&
            rightType.pointType.toString() == "System.Any" &&
            leftType.pointType.toString() != "System.Any"
    ) {
        rightElm = expandIntervalToType(rightElm, leftType.pointType)
    }

    return when (val phrase = expression.phrase) {
        is IncludesIntervalPhrase -> emitIncludesPhrase(phrase, expression, leftElm, rightElm)
        is IncludedInIntervalPhrase -> emitIncludedInPhrase(phrase, expression, leftElm, rightElm)
        is BeforeOrAfterIntervalPhrase ->
            emitBeforeOrAfterPhrase(phrase, expression, leftElm, rightElm)
        is MeetsIntervalPhrase -> emitMeetsPhrase(phrase, leftElm, rightElm)
        is OverlapsIntervalPhrase -> emitOverlapsPhrase(phrase, leftElm, rightElm)
        is StartsIntervalPhrase -> emitStartsPhrase(phrase, leftElm, rightElm)
        is EndsIntervalPhrase -> emitEndsPhrase(phrase, leftElm, rightElm)
        is ConcurrentIntervalPhrase -> emitConcurrentPhrase(phrase, leftElm, rightElm)
        is WithinIntervalPhrase ->
            throw ElmEmitter.UnsupportedNodeException(
                "WithinIntervalPhrase ('within N days of') is not yet supported."
            )
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Interval operator phrase '${phrase::class.simpleName}' is not yet supported."
            )
    }
}

/** Apply a boundary selector to an operand, wrapping in Start() or End() as needed. */
private fun applyBoundary(
    operand: ElmExpression,
    boundary: IntervalBoundarySelector?,
): ElmExpression {
    return when (boundary) {
        IntervalBoundarySelector.START -> Start().apply { this.operand = operand }
        IntervalBoundarySelector.END -> End().apply { this.operand = operand }
        IntervalBoundarySelector.OCCURS,
        null -> operand
    }
}

private fun EmissionContext.emitIncludesPhrase(
    phrase: IncludesIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val right = applyBoundary(rightElm, phrase.rightBoundary)
    // Determine if the right operand is a point (not a list/interval)
    // Empty list literals ({}) are treated as element type by legacy operator resolution
    // when the left operand has a concrete (non-Any) element type.
    // When the left is Interval<Any>, the right (any type) is treated as element (Contains).
    val leftType = semanticModel[expression.left]
    val leftIsAnyInterval =
        leftType is IntervalType && leftType.pointType.toString() == "System.Any"
    val isPointRight =
        (phrase.rightBoundary != null && phrase.rightBoundary != IntervalBoundarySelector.OCCURS) ||
            isElementType(expression.right) ||
            (isEmptyListLiteral(expression.right) && hasConcreteListElementType(expression.left)) ||
            leftIsAnyInterval
    return if (phrase.proper) {
        if (isPointRight) {
            ProperContains().apply {
                operand = mutableListOf(leftElm, right)
                precision?.let { this.precision = it }
            }
        } else {
            ProperIncludes().apply {
                operand = mutableListOf(leftElm, right)
                precision?.let { this.precision = it }
            }
        }
    } else {
        if (isPointRight) {
            Contains().apply {
                operand = mutableListOf(leftElm, right)
                precision?.let { this.precision = it }
            }
        } else {
            Includes().apply {
                operand = mutableListOf(leftElm, right)
                precision?.let { this.precision = it }
            }
        }
    }
}

private fun EmissionContext.emitIncludedInPhrase(
    phrase: IncludedInIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val left = applyBoundary(leftElm, phrase.leftBoundary)
    // Determine if the left operand is a point (not a list/interval)
    val isPointLeft =
        (phrase.leftBoundary != null && phrase.leftBoundary != IntervalBoundarySelector.OCCURS) ||
            isElementType(expression.left)
    return if (phrase.proper) {
        if (isPointLeft) {
            ProperIn().apply {
                operand = mutableListOf(left, rightElm)
                precision?.let { this.precision = it }
            }
        } else {
            ProperIncludedIn().apply {
                operand = mutableListOf(left, rightElm)
                precision?.let { this.precision = it }
            }
        }
    } else {
        if (isPointLeft) {
            In().apply {
                operand = mutableListOf(left, rightElm)
                precision?.let { this.precision = it }
            }
        } else {
            IncludedIn().apply {
                operand = mutableListOf(left, rightElm)
                precision?.let { this.precision = it }
            }
        }
    }
}

private fun EmissionContext.emitBeforeOrAfterPhrase(
    phrase: BeforeOrAfterIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    if (phrase.offset != null) {
        throw ElmEmitter.UnsupportedNodeException(
            "Quantity offsets on before/after (e.g., '3 days before') are not yet supported."
        )
    }
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    var left = applyBoundary(leftElm, phrase.leftBoundary)
    var right = applyBoundary(rightElm, phrase.rightBoundary)

    // Point-interval promotion: when one operand is a point and the other is an interval,
    // wrap the point in If(IsNull(point), Null, Interval[point, point])
    val leftType = semanticModel[expression.left]
    val rightType = semanticModel[expression.right]
    if (leftType != null && leftType !is IntervalType && rightType is IntervalType) {
        left = promotePointToInterval(left)
    } else if (rightType != null && rightType !is IntervalType && leftType is IntervalType) {
        right = promotePointToInterval(right)
    }

    val ops = mutableListOf(left, right)

    val inclusive = phrase.relationship.inclusive
    return when (phrase.relationship.direction) {
        TemporalRelationshipDirection.BEFORE ->
            if (inclusive) {
                SameOrBefore().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            } else {
                Before().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            }
        TemporalRelationshipDirection.AFTER ->
            if (inclusive) {
                SameOrAfter().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            } else {
                After().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            }
    }
}

private fun emitConcurrentPhrase(
    phrase: ConcurrentIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val left = applyBoundary(leftElm, phrase.leftBoundary)
    val right = applyBoundary(rightElm, phrase.rightBoundary)
    val ops = mutableListOf(left, right)

    return when (phrase.qualifier) {
        ConcurrentQualifier.AS ->
            SameAs().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        ConcurrentQualifier.OR_BEFORE ->
            SameOrBefore().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        ConcurrentQualifier.OR_AFTER ->
            SameOrAfter().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
    }
}

private fun emitMeetsPhrase(
    phrase: MeetsIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val ops = mutableListOf(leftElm, rightElm)
    return when (phrase.direction) {
        TemporalRelationshipDirection.BEFORE ->
            MeetsBefore().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        TemporalRelationshipDirection.AFTER ->
            MeetsAfter().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        null ->
            Meets().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
    }
}

private fun emitOverlapsPhrase(
    phrase: OverlapsIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val ops = mutableListOf(leftElm, rightElm)
    return when (phrase.direction) {
        TemporalRelationshipDirection.BEFORE ->
            OverlapsBefore().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        TemporalRelationshipDirection.AFTER ->
            OverlapsAfter().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        null ->
            Overlaps().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
    }
}

private fun emitStartsPhrase(
    phrase: StartsIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return Starts().apply {
        operand = mutableListOf(leftElm, rightElm)
        precision?.let { this.precision = it }
    }
}

private fun emitEndsPhrase(
    phrase: EndsIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return Ends().apply {
        operand = mutableListOf(leftElm, rightElm)
        precision?.let { this.precision = it }
    }
}

/**
 * Returns true if the expression's resolved type is an element type (not a list or interval). Used
 * to determine whether `includes` should emit as `Contains` (element) vs. `Includes` (collection)
 * and similarly for `included in` → `In` vs. `IncludedIn`.
 */
private fun EmissionContext.isElementType(expression: org.hl7.cql.ast.Expression): Boolean {
    val type = semanticModel[expression] ?: return false
    return type !is ListType && type !is IntervalType
}

/** Check if an AST expression is a null literal. */
internal fun isNullLiteralExpr(expr: org.hl7.cql.ast.Expression): Boolean {
    if (expr !is org.hl7.cql.ast.LiteralExpression) return false
    return expr.literal is org.hl7.cql.ast.NullLiteral
}

/** Check if an AST expression is an empty list literal (`{}`). */
internal fun isEmptyListLiteral(expr: org.hl7.cql.ast.Expression): Boolean {
    if (expr !is org.hl7.cql.ast.LiteralExpression) return false
    val literal = expr.literal
    return literal is org.hl7.cql.ast.ListLiteral && literal.elements.isEmpty()
}

/**
 * Check if an expression's type is a List with a concrete (non-Any) element type. Used to
 * determine whether an empty list on the other side of `includes` should be treated as an element.
 */
private fun EmissionContext.hasConcreteListElementType(
    expression: org.hl7.cql.ast.Expression
): Boolean {
    val type = semanticModel[expression] ?: return false
    if (type !is ListType) return false
    val elemType = type.elementType
    // "Any" is the default for untyped empty lists - a concrete type means the list has elements
    return elemType.toString() != "System.Any"
}

/**
 * Expand an `Interval<Any>` expression by extracting Property paths and casting to the target point
 * type. Produces: `Interval(As(T, low), lowClosed, As(T, high), highClosed)`.
 */
private fun EmissionContext.expandIntervalToType(
    intervalExpr: ElmExpression,
    targetPointType: org.hl7.cql.model.DataType,
): ElmExpression {
    val asQName = operatorRegistry.typeBuilder.dataTypeToQName(targetPointType)
    return org.hl7.elm.r1.Interval().apply {
        low =
            org.hl7.elm.r1.As().apply {
                asType = asQName
                operand =
                    org.hl7.elm.r1.Property().apply {
                        path = "low"
                        source = intervalExpr
                    }
            }
        lowClosedExpression =
            org.hl7.elm.r1.Property().apply {
                path = "lowClosed"
                source = intervalExpr
            }
        high =
            org.hl7.elm.r1.As().apply {
                asType = asQName
                operand =
                    org.hl7.elm.r1.Property().apply {
                        path = "high"
                        source = intervalExpr
                    }
            }
        highClosedExpression =
            org.hl7.elm.r1.Property().apply {
                path = "highClosed"
                source = intervalExpr
            }
    }
}

/**
 * Promote a point expression to a degenerate interval: If(IsNull(point), Null, Interval[point,
 * point]). This matches legacy behavior for point-interval comparisons.
 */
private fun promotePointToInterval(point: ElmExpression): ElmExpression {
    return org.hl7.elm.r1.If().apply {
        condition = org.hl7.elm.r1.IsNull().apply { operand = point }
        then = org.hl7.elm.r1.Null()
        `else` =
            org.hl7.elm.r1.Interval().apply {
                low = point
                high = point
                lowClosed = true
                highClosed = true
            }
    }
}
