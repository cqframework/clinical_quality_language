package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Interval;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;

public class IntervalEvaluator {
    public static Object internalEvaluate(Interval interval, State state, CqlEngine visitor) {
        Object low = interval.getLow() != null ? visitor.validateOperand(visitor.visitExpression(interval.getLow(), state)) : null;

        Object lowClosedObj = false;
        if (interval.getLowClosedExpression() != null) {
            lowClosedObj = visitor.validateOperand(visitor.visitExpression(interval.getLowClosedExpression(), state));
        }

        Boolean lowClosed = (interval.getLowClosedExpression() != null && lowClosedObj != null) ?
                (Boolean) lowClosedObj : interval.isLowClosed();

        Object high = interval.getHigh() != null ? visitor.validateOperand(visitor.visitExpression(interval.getHigh(), state)) : null;

        Object highClosedObj = false;
        if (interval.getHighClosedExpression() != null) {
            highClosedObj = visitor.validateOperand(visitor.visitExpression(interval.getHighClosedExpression(), state));
        }

        Boolean highClosed = (interval.getHighClosedExpression() != null && highClosedObj != null) ?
                (Boolean) highClosedObj : interval.isHighClosed();

        // An interval with no boundaries is not an interval
        // TODO: the spec states that it is possible to have an interval with null boundaries, but the ELM is not providing a way to get the Interval type
        if (low == null && high == null) {
            return null;
        }

        return new org.opencds.cqf.cql.engine.runtime.Interval(low, lowClosed == null ? true : lowClosed, high, highClosed == null ?
                true : highClosed);
    }
}
