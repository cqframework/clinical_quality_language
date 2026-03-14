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
    val leftElm = emitExpression(expression.left)
    val rightElm = emitExpression(expression.right)

    return when (val phrase = expression.phrase) {
        is IncludesIntervalPhrase -> emitIncludesPhrase(phrase, leftElm, rightElm)
        is IncludedInIntervalPhrase -> emitIncludedInPhrase(phrase, leftElm, rightElm)
        is BeforeOrAfterIntervalPhrase -> emitBeforeOrAfterPhrase(phrase, leftElm, rightElm)
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

private fun emitIncludesPhrase(
    phrase: IncludesIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val right = applyBoundary(rightElm, phrase.rightBoundary)
    // When the right operand has a boundary selector (making it a point), switch to
    // Contains/ProperContains
    val isPointRight =
        phrase.rightBoundary != null && phrase.rightBoundary != IntervalBoundarySelector.OCCURS
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

private fun emitIncludedInPhrase(
    phrase: IncludedInIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val left = applyBoundary(leftElm, phrase.leftBoundary)
    // When the left operand has a boundary selector (making it a point), switch to In/ProperIn
    val isPointLeft =
        phrase.leftBoundary != null && phrase.leftBoundary != IntervalBoundarySelector.OCCURS
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

private fun emitBeforeOrAfterPhrase(
    phrase: BeforeOrAfterIntervalPhrase,
    leftElm: ElmExpression,
    rightElm: ElmExpression,
): ElmExpression {
    if (phrase.offset != null) {
        throw ElmEmitter.UnsupportedNodeException(
            "Quantity offsets on before/after (e.g., '3 days before') are not yet supported."
        )
    }
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    val left = applyBoundary(leftElm, phrase.leftBoundary)
    val right = applyBoundary(rightElm, phrase.rightBoundary)
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
