@file:Suppress("TooManyFunctions")

package org.cqframework.cql.cql2elm.codegen

import org.hl7.cql.ast.BeforeOrAfterIntervalPhrase
import org.hl7.cql.ast.EndsIntervalPhrase
import org.hl7.cql.ast.IncludedInIntervalPhrase
import org.hl7.cql.ast.IncludesIntervalPhrase
import org.hl7.cql.ast.IntervalRelationExpression
import org.hl7.cql.ast.MeetsIntervalPhrase
import org.hl7.cql.ast.OverlapsIntervalPhrase
import org.hl7.cql.ast.StartsIntervalPhrase
import org.hl7.cql.ast.TemporalRelationshipDirection
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.Ends
import org.hl7.elm.r1.Expression as ElmExpression
import org.hl7.elm.r1.IncludedIn
import org.hl7.elm.r1.Includes
import org.hl7.elm.r1.Meets
import org.hl7.elm.r1.MeetsAfter
import org.hl7.elm.r1.MeetsBefore
import org.hl7.elm.r1.Overlaps
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.OverlapsBefore
import org.hl7.elm.r1.ProperIncludedIn
import org.hl7.elm.r1.ProperIncludes
import org.hl7.elm.r1.Starts

/** Emit an [IntervalRelationExpression] by dispatching on the phrase type. */
@Suppress("CyclomaticComplexMethod")
internal fun EmissionContext.emitIntervalRelation(
    expression: IntervalRelationExpression
): ElmExpression {
    val leftElm = emitExpression(expression.left)
    val rightElm = emitExpression(expression.right)
    val ops = mutableListOf(leftElm, rightElm)

    return when (val phrase = expression.phrase) {
        is IncludesIntervalPhrase -> emitIncludesPhrase(phrase, ops)
        is IncludedInIntervalPhrase -> emitIncludedInPhrase(phrase, ops)
        is BeforeOrAfterIntervalPhrase -> emitBeforeOrAfterPhrase(phrase, ops)
        is MeetsIntervalPhrase -> emitMeetsPhrase(phrase, ops)
        is OverlapsIntervalPhrase -> emitOverlapsPhrase(phrase, ops)
        is StartsIntervalPhrase -> emitStartsPhrase(phrase, ops)
        is EndsIntervalPhrase -> emitEndsPhrase(phrase, ops)
        else ->
            throw ElmEmitter.UnsupportedNodeException(
                "Interval operator phrase '${phrase::class.simpleName}' is not yet supported."
            )
    }
}

private fun emitIncludesPhrase(
    phrase: IncludesIntervalPhrase,
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return if (phrase.proper) {
        ProperIncludes().apply {
            operand = ops
            precision?.let { this.precision = it }
        }
    } else {
        Includes().apply {
            operand = ops
            precision?.let { this.precision = it }
        }
    }
}

private fun emitIncludedInPhrase(
    phrase: IncludedInIntervalPhrase,
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return if (phrase.proper) {
        ProperIncludedIn().apply {
            operand = ops
            precision?.let { this.precision = it }
        }
    } else {
        IncludedIn().apply {
            operand = ops
            precision?.let { this.precision = it }
        }
    }
}

private fun emitBeforeOrAfterPhrase(
    phrase: BeforeOrAfterIntervalPhrase,
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return when (phrase.relationship.direction) {
        TemporalRelationshipDirection.BEFORE ->
            Before().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
        TemporalRelationshipDirection.AFTER ->
            After().apply {
                operand = ops
                precision?.let { this.precision = it }
            }
    }
}

private fun emitMeetsPhrase(
    phrase: MeetsIntervalPhrase,
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
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
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
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
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return Starts().apply {
        operand = ops
        precision?.let { this.precision = it }
    }
}

private fun emitEndsPhrase(
    phrase: EndsIntervalPhrase,
    ops: MutableList<ElmExpression>,
): ElmExpression {
    val precision = phrase.precision?.let { precisionStringToEnum(it) }
    return Ends().apply {
        operand = ops
        precision?.let { this.precision = it }
    }
}
