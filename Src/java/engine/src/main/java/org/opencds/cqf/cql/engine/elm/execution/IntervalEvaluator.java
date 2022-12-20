package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class IntervalEvaluator extends org.cqframework.cql.elm.execution.Interval {

    @Override
    protected Object internalEvaluate(Context context) {
        Object low = getLow() != null ? getLow().evaluate(context) : null;
        Boolean lowClosed = getLowClosedExpression() != null ? (Boolean)getLowClosedExpression().evaluate(context) : this.lowClosed;
        Object high = getHigh() != null ? getHigh().evaluate(context) : null;
        Boolean highClosed = getHighClosedExpression() != null ? (Boolean)getHighClosedExpression().evaluate(context) : this.highClosed;

        // An interval with no boundaries is not an interval
        // TODO: the spec states that it is possible to have an interval with null boundaries, but the ELM is not providing a way to get the Interval type
        if (low == null && high == null) {
            return null;
        }

        return new org.opencds.cqf.cql.engine.runtime.Interval(low, lowClosed == null ? true : lowClosed, high, highClosed == null ? true : highClosed);
    }
}
