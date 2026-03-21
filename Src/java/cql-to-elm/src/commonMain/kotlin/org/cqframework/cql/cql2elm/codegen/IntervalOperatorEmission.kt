@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BeforeOrAfterIntervalPhrase
import org.hl7.cql.ast.ConcurrentIntervalPhrase
import org.hl7.cql.ast.ConcurrentQualifier
import org.hl7.cql.ast.EndsIntervalPhrase
import org.hl7.cql.ast.ImplicitCastExpression
import org.hl7.cql.ast.IncludedInIntervalPhrase
import org.hl7.cql.ast.IncludesIntervalPhrase
import org.hl7.cql.ast.IntervalBoundarySelector
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.IntervalTypeSpecifier
import org.hl7.cql.ast.ListTypeSpecifier
import org.hl7.cql.ast.MeetsIntervalPhrase
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.OverlapsIntervalPhrase
import org.hl7.cql.ast.StartsIntervalPhrase
import org.hl7.cql.ast.TemporalRelationshipDirection
import org.hl7.cql.ast.WithinIntervalPhrase
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Before
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

/**
 * Emit an [IntervalRelationExpression] by dispatching on the phrase type. Children are pre-folded.
 *
 * Null-As wrapping is handled by ConversionInserter before emission.
 */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitIntervalRelation(
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    // Interval<Any> expansion is handled by Normalizer. Operands arrive processed.
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
        // WithinIntervalPhrase is fully normalized by Normalizer into
        // MembershipExpression(IN, ...) — it never reaches here.
        is WithinIntervalPhrase ->
            throw ElmEmitter.UnsupportedNodeException(
                "WithinIntervalPhrase should have been normalized to MembershipExpression."
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

/**
 * Emit an includes phrase. Non-proper point cases are normalized to MembershipExpression(CONTAINS)
 * by Normalizer. This handler handles: proper cases (ProperContains/ProperIncludes) and non-proper
 * non-point cases (Includes).
 */
private fun EmissionContext.emitIncludesPhrase(
    phrase: IncludesIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    if (phrase.proper) {
        // Proper cases need type check for ProperContains vs ProperIncludes
        val leftType = semanticModel[expression.left]
        val leftIsAnyInterval =
            leftType is IntervalType && leftType.pointType.toString() == "System.Any"
        val isPointRight =
            (phrase.rightBoundary != null &&
                phrase.rightBoundary != IntervalBoundarySelector.OCCURS) ||
                isElementType(expression.right) ||
                (isEmptyListLiteral(expression.right) &&
                    hasConcreteListElementType(expression.left)) ||
                leftIsAnyInterval
        return if (isPointRight) {
            ProperContains().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
        } else {
            ProperIncludes().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
        }
    }
    // Non-proper non-point: Includes (point cases normalized to MembershipExpression by Normalizer)
    return Includes().apply {
        operand = mutableListOf(leftElm, rightElm)
        precision?.let { this.precision = it }
    }
}

/**
 * Emit an includedIn phrase. Non-proper point cases are normalized to MembershipExpression(IN) by
 * Normalizer. This handler handles: proper cases (ProperIn/ProperIncludedIn) and non-proper
 * non-point cases (IncludedIn).
 */
private fun EmissionContext.emitIncludedInPhrase(
    phrase: IncludedInIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    if (phrase.proper) {
        val isPointLeft =
            (phrase.leftBoundary != null &&
                phrase.leftBoundary != IntervalBoundarySelector.OCCURS) ||
                isElementType(expression.left)
        return if (isPointLeft) {
            ProperIn().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
        } else {
            ProperIncludedIn().apply {
                operand = mutableListOf(leftElm, rightElm)
                precision?.let { this.precision = it }
            }
        }
    }
    // Non-proper non-point: IncludedIn (point cases normalized by Normalizer)
    return IncludedIn().apply {
        operand = mutableListOf(leftElm, rightElm)
        precision?.let { this.precision = it }
    }
}

/**
 * Emit a before/after phrase. Boundary application, point-interval promotion, and direction-based
 * interval extraction are handled by [Normalizer] — operands arrive fully processed. This handler
 * is purely structural: map the phrase to the correct ELM operator and apply quantity offset
 * arithmetic.
 */
@Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "LongMethod")
private fun EmissionContext.emitBeforeOrAfterPhrase(
    phrase: BeforeOrAfterIntervalPhrase,
    expression: IntervalRelationExpression,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val isBefore = phrase.relationship.direction == TemporalRelationshipDirection.BEFORE
    val isInclusive = phrase.relationship.inclusive

    if (phrase.offset == null) {
        // No offset — operands normalized with boundaries + promotion by Normalizer
        val ops = mutableListOf(leftElm, rightElm)
        return if (isInclusive) {
            if (isBefore)
                SameOrBefore().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            else
                SameOrAfter().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
        } else {
            if (isBefore)
                Before().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            else
                After().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
        }
    }

    // Offset — operands normalized with boundaries + direction extraction by Normalizer
    val offset = phrase.offset!!
    val qty = emitLiteral(offset.quantity)
    val left = leftElm
    val right = rightElm

    val isOrMore = offset.offsetQualifier == org.hl7.cql.ast.OffsetRelativeQualifier.OR_MORE
    val isMoreThan =
        offset.exclusiveQualifier == org.hl7.cql.ast.ExclusiveRelativeQualifier.MORE_THAN
    val isOrLess = offset.offsetQualifier == org.hl7.cql.ast.OffsetRelativeQualifier.OR_LESS
    val isLessThan =
        offset.exclusiveQualifier == org.hl7.cql.ast.ExclusiveRelativeQualifier.LESS_THAN

    if (isOrMore || isMoreThan) {
        // "or more" / "more than": Before/SameOrBefore(left, Subtract/Add(right, qty))
        val isOffsetInclusive = isOrMore
        val adjusted =
            if (isBefore) org.hl7.elm.r1.Subtract().apply { operand = mutableListOf(right, qty) }
            else org.hl7.elm.r1.Add().apply { operand = mutableListOf(right, qty) }
        val ops = mutableListOf(left, adjusted as ElmExpression)
        return if (isOffsetInclusive) {
            if (isBefore)
                SameOrBefore().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            else
                SameOrAfter().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
        } else {
            if (isBefore)
                Before().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
            else
                After().apply {
                    operand = ops
                    precision?.let { this.precision = it }
                }
        }
    }

    if (isOrLess || isLessThan) {
        // "or less" / "less than": In(left, Interval(Subtract/Add(right, qty), right))
        val isOffsetInclusive = isOrLess
        val lower: ElmExpression
        val upper: ElmExpression
        if (isBefore) {
            lower = org.hl7.elm.r1.Subtract().apply { operand = mutableListOf(right, qty) }
            upper = right
        } else {
            lower = right
            upper =
                org.hl7.elm.r1.Add().apply {
                    operand = mutableListOf(right, emitLiteral(offset.quantity))
                }
        }
        val interval =
            org.hl7.elm.r1.Interval().apply {
                low = lower
                high = upper
                lowClosed = if (isBefore) isOffsetInclusive else isInclusive
                highClosed = if (isBefore) isInclusive else isOffsetInclusive
            }
        val inExpr =
            In().apply {
                operand = mutableListOf(left, interval)
                precision?.let { this.precision = it }
            }
        // Add null check when offset or comparison is inclusive
        return if (isOffsetInclusive || isInclusive) {
            org.hl7.elm.r1.And().apply {
                operand =
                    mutableListOf(
                        inExpr,
                        org.hl7.elm.r1.Not().apply {
                            operand = org.hl7.elm.r1.IsNull().apply { this.operand = right }
                        },
                    )
            }
        } else {
            inExpr
        }
    }

    // Exact offset (no qualifier): SameAs(left, Subtract/Add(right, qty))
    val adjusted =
        if (isBefore) org.hl7.elm.r1.Subtract().apply { operand = mutableListOf(right, qty) }
        else org.hl7.elm.r1.Add().apply { operand = mutableListOf(right, qty) }
    return SameAs().apply {
        operand = mutableListOf(left, adjusted as ElmExpression)
        precision?.let { this.precision = it }
    }
}

private fun emitConcurrentPhrase(
    phrase: ConcurrentIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    // Boundaries applied by Normalizer.
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val ops = mutableListOf(leftElm, rightElm)

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

// emitWithinPhrase — deleted. Within is fully normalized by Normalizer
// into MembershipExpression(IN, ...) with arithmetic and null check.

/**
 * Returns true if the expression's resolved type is an element type (not a list or interval). Used
 * to determine whether `includes` should emit as `Contains` (element) vs. `Includes` (collection)
 * and similarly for `included in` → `In` vs. `IncludedIn`.
 */
private fun EmissionContext.isElementType(expression: org.hl7.cql.ast.Expression): Boolean {
    val type = semanticModel[expression]
    if (type != null) return type !is ListType && type !is IntervalType
    // ImplicitCastExpression inserted by ConversionInserter (null-wrapping) is not in
    // semanticModel.
    // Determine element-ness from the target type specifier.
    if (expression is ImplicitCastExpression) {
        return when (expression.type) {
            is NamedTypeSpecifier -> true // Named types are always points
            is ListTypeSpecifier -> false
            is IntervalTypeSpecifier -> false
            else -> false
        }
    }
    return false
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
 * Check if an expression's type is a List with a concrete (non-Any) element type. Used to determine
 * whether an empty list on the other side of `includes` should be treated as an element.
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
    val asQName = dataTypeToQName(targetPointType)
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
