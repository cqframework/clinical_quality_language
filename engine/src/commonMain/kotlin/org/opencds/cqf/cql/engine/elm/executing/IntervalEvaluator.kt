package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.As
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Value

object IntervalEvaluator {
    fun internalEvaluate(
        interval: Interval?,
        state: State?,
        visitor: ElmLibraryVisitor<Value?, State?>,
    ): Value? {
        val low =
            if (interval!!.low != null) visitor.visitExpression(interval.low!!, state) else null

        var lowClosedObj: Boolean? = Boolean.FALSE
        if (interval.lowClosedExpression != null) {
            lowClosedObj =
                visitor.visitExpression(interval.lowClosedExpression!!, state) as Boolean?
        }

        val lowClosed =
            if (interval.lowClosedExpression != null && lowClosedObj != null) lowClosedObj.value
            else interval.isLowClosed()

        val high =
            if (interval.high != null) visitor.visitExpression(interval.high!!, state) else null

        var highClosedObj: Boolean? = Boolean.FALSE
        if (interval.highClosedExpression != null) {
            highClosedObj =
                visitor.visitExpression(interval.highClosedExpression!!, state) as Boolean?
        }

        val highClosed =
            if (interval.highClosedExpression != null && highClosedObj != null) highClosedObj.value
            else interval.isHighClosed()

        if (low == null && high == null) {
            return maxInterval(interval, lowClosed == true && highClosed == true, state)
        }

        return org.opencds.cqf.cql.engine.runtime.Interval(
            low,
            lowClosed ?: true,
            high,
            highClosed ?: true,
            state = state,
        )
    }

    /**
     * Interval[null, null]: a closed null boundary stands for the minimum or maximum of the point
     * type (Start, End), so this is the interval over the whole point type, when the ELM says what
     * that type is and the type has a minimum and maximum. Otherwise there is no interval to build.
     */
    private fun maxInterval(
        interval: Interval,
        closed: kotlin.Boolean,
        state: State?,
    ): org.opencds.cqf.cql.engine.runtime.Interval? {
        val pointType =
            if (closed) pointType(interval)?.let { state?.environment?.fixupQName(it) ?: it }
            else null
        return if (pointType == null || pointType !in BOUNDED_POINT_TYPES) null
        else
            org.opencds.cqf.cql.engine.runtime.Interval(
                MinValueEvaluator.minValue(pointType),
                true,
                MaxValueEvaluator.maxValue(pointType),
                true,
                state = state,
            )
    }

    private const val SYSTEM = "urn:hl7-org:elm-types:r1"

    /** Point types with a minimum and maximum value. Quantity has no standard one. */
    private val BOUNDED_POINT_TYPES =
        setOf("Integer", "Long", "Decimal", "Date", "DateTime", "Time").map { QName(SYSTEM, it) }

    /**
     * The declared point type of an interval whose boundaries are both null: the type a boundary is
     * cast to (`Interval[null as Integer, null as Integer]`, or the implicit cast the translator
     * adds when an operator needs `Interval<Integer>`), or the interval's result type.
     */
    private fun pointType(interval: Interval): QName? {
        val declared =
            castType(interval.low)
                ?: castType(interval.high)
                ?: ((interval.resultTypeSpecifier as? IntervalTypeSpecifier)?.pointType
                        as? NamedTypeSpecifier)
                    ?.name
        return declared?.takeUnless { it == QName(SYSTEM, "Any") }
    }

    private fun castType(boundary: Expression?): QName? =
        (boundary as? As)?.let { it.asType ?: (it.asTypeSpecifier as? NamedTypeSpecifier)?.name }
}
