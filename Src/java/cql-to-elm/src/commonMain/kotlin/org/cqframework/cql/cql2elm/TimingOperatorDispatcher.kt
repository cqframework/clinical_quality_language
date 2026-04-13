package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.elm.r1.After
import org.hl7.elm.r1.Before
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.DateTimePrecision
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.End
import org.hl7.elm.r1.Ends
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Meets
import org.hl7.elm.r1.MeetsAfter
import org.hl7.elm.r1.MeetsBefore
import org.hl7.elm.r1.Overlaps
import org.hl7.elm.r1.OverlapsAfter
import org.hl7.elm.r1.OverlapsBefore
import org.hl7.elm.r1.SameAs
import org.hl7.elm.r1.SameOrAfter
import org.hl7.elm.r1.SameOrBefore
import org.hl7.elm.r1.Start
import org.hl7.elm.r1.Starts

/**
 * Factors out the repetitive construct-track-resolve pattern that dominates CQL temporal operator
 * visitors.
 *
 * The dispatch table maps operator names ("Before", "SameOrAfter", "Meets", …) to their ELM
 * [BinaryExpression] constructors. `buildTemporal()` wraps the three-step pattern:
 * 1. construct the ELM node via the table
 * 2. set `precision` (type-specific — each temporal binary declares its own precision field)
 * 3. set operands, track, and resolve the call
 *
 * Unary `Start` / `End` wrappers around a timing operand are factored into `takeStart` and
 * `takeEnd`, which mutate the relevant side of a
 * [org.cqframework.cql.cql2elm.model.TimingOperatorContext]-like caller-owned state in place.
 */
class TimingOperatorDispatcher(
    private val libraryBuilder: LibraryBuilder,
    private val of: IdObjectFactory,
    private val track: (Element, ParseTree) -> TrackBack?,
    private val trackFromElement: (Element, Element) -> TrackBack?,
) {
    /**
     * Build a temporal binary operator node, set its precision, wire operands, track, and resolve.
     * Returns the constructed node.
     */
    fun buildTemporal(
        operatorName: String,
        left: Expression,
        right: Expression,
        precision: DateTimePrecision? = null,
        allowPromotionAndDemotion: Boolean = false,
    ): BinaryExpression {
        val ctor =
            requireNotNull(temporalFactories[operatorName]) {
                "No temporal operator factory for '$operatorName'"
            }
        val op = ctor()
        applyPrecision(op, precision)
        op.withOperand(listOf(left, right))
        libraryBuilder.resolveBinaryCall(
            "System",
            operatorName,
            op,
            true,
            allowPromotionAndDemotion,
        )
        return op
    }

    /** Wrap `source` in a `Start`, track it against `trackNode`, and resolve. */
    fun takeStart(source: Expression, trackNode: ParseTree): Start {
        val start = of.createStart().withOperand(source)
        track(start, trackNode)
        libraryBuilder.resolveCall("System", "Start", start)
        return start
    }

    /** Wrap `source` in a `Start`, copy trackbacks from [trackFrom], and resolve. */
    fun takeStart(source: Expression, trackFrom: Element): Start {
        val start = of.createStart().withOperand(source)
        trackFromElement(start, trackFrom)
        libraryBuilder.resolveCall("System", "Start", start)
        return start
    }

    /** Wrap `source` in an `End`, track it against `trackNode`, and resolve. */
    fun takeEnd(source: Expression, trackNode: ParseTree): End {
        val end = of.createEnd().withOperand(source)
        track(end, trackNode)
        libraryBuilder.resolveCall("System", "End", end)
        return end
    }

    /** Wrap `source` in an `End`, copy trackbacks from [trackFrom], and resolve. */
    fun takeEnd(source: Expression, trackFrom: Element): End {
        val end = of.createEnd().withOperand(source)
        trackFromElement(end, trackFrom)
        libraryBuilder.resolveCall("System", "End", end)
        return end
    }

    private val temporalFactories: Map<String, () -> BinaryExpression> =
        mapOf(
            "Before" to of::createBefore,
            "After" to of::createAfter,
            "SameAs" to of::createSameAs,
            "SameOrBefore" to of::createSameOrBefore,
            "SameOrAfter" to of::createSameOrAfter,
            "Meets" to of::createMeets,
            "MeetsBefore" to of::createMeetsBefore,
            "MeetsAfter" to of::createMeetsAfter,
            "Overlaps" to of::createOverlaps,
            "OverlapsBefore" to of::createOverlapsBefore,
            "OverlapsAfter" to of::createOverlapsAfter,
            "Starts" to of::createStarts,
            "Ends" to of::createEnds,
        )

    @Suppress("CyclomaticComplexMethod")
    private fun applyPrecision(op: BinaryExpression, precision: DateTimePrecision?) {
        if (precision == null) return
        when (op) {
            is Before -> op.precision = precision
            is After -> op.precision = precision
            is SameAs -> op.precision = precision
            is SameOrBefore -> op.precision = precision
            is SameOrAfter -> op.precision = precision
            is Meets -> op.precision = precision
            is MeetsBefore -> op.precision = precision
            is MeetsAfter -> op.precision = precision
            is Overlaps -> op.precision = precision
            is OverlapsBefore -> op.precision = precision
            is OverlapsAfter -> op.precision = precision
            is Starts -> op.precision = precision
            is Ends -> op.precision = precision
            else -> error("Operator ${op::class.simpleName} does not support precision")
        }
    }
}
