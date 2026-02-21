package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Interval
import org.opencds.cqf.cql.engine.execution.State

object IntervalEvaluator {
    fun internalEvaluate(
        interval: Interval?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val low =
            if (interval!!.low != null) visitor.visitExpression(interval.low!!, state) else null

        var lowClosedObj: Any? = false
        if (interval.lowClosedExpression != null) {
            lowClosedObj = visitor.visitExpression(interval.lowClosedExpression!!, state)
        }

        val lowClosed =
            if (interval.lowClosedExpression != null && lowClosedObj != null)
                lowClosedObj as Boolean
            else interval.isLowClosed()

        val high =
            if (interval.high != null) visitor.visitExpression(interval.high!!, state) else null

        var highClosedObj: Any? = false
        if (interval.highClosedExpression != null) {
            highClosedObj = visitor.visitExpression(interval.highClosedExpression!!, state)
        }

        val highClosed =
            if (interval.highClosedExpression != null && highClosedObj != null)
                highClosedObj as Boolean
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
