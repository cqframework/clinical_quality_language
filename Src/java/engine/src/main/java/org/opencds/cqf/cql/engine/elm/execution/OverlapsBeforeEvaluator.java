package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
overlaps before _precision_ (left Interval<T>, right Interval<T>) Boolean

The operator overlaps before returns true if the first interval overlaps the second and starts before it.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class OverlapsBeforeEvaluator extends org.cqframework.cql.elm.execution.OverlapsBefore {

    public static Object overlapsBefore(Object left, Object right, String precision, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object rightStart = ((Interval) right).getStart();

            if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal) {
                return AndEvaluator.and(
                        BeforeEvaluator.before(leftStart, rightStart, precision, context),
                        OverlapsEvaluator.overlaps(left, right, precision, context)
                );
            }

            else {
                return AndEvaluator.and(
                        LessEvaluator.less(leftStart, rightStart, context),
                        OverlapsEvaluator.overlaps(left, right, precision, context)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "OverlapsBefore(Interval<T>, Interval<T>)",
                String.format("OverlapsBefore(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return overlapsBefore(left, right, precision, context);
    }
}
