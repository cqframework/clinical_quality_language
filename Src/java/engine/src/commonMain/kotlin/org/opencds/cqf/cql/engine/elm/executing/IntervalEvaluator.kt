package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Interval
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType

object IntervalEvaluator {
    fun internalEvaluate(
        interval: Interval?,
        state: State?,
        visitor: ElmLibraryVisitor<CqlType?, State?>,
    ): CqlType? {
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

        // An interval with no boundaries is not an interval
        // TODO: the spec states that it is possible to have an interval with null boundaries, but
        // the ELM is not
        // providing a way to get the Interval type
        if (low == null && high == null) {
            return null
        }

        return org.opencds.cqf.cql.engine.runtime.Interval(
            low,
            lowClosed ?: true,
            high,
            highClosed ?: true,
            state,
        )
    }
}
