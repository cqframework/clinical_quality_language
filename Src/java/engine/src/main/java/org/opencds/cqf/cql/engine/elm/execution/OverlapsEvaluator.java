package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
overlaps _precision_ (left Interval<T>, right Interval<T>) Boolean

The overlaps operator returns true if the first interval overlaps the second.
    More precisely, if the ending point of the first interval is greater than or equal to the
    starting point of the second interval, and the starting point of the first interval is
    less than or equal to the ending point of the second interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class OverlapsEvaluator extends org.cqframework.cql.elm.execution.Overlaps {

    public static Boolean overlaps(Object left, Object right, String precision, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object leftEnd = ((Interval) left).getEnd();
            Object rightStart = ((Interval) right).getStart();
            Object rightEnd = ((Interval) right).getEnd();

            if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal) {
                return AndEvaluator.and(
                        SameOrBeforeEvaluator.sameOrBefore(leftStart, rightEnd, precision, context),
                        SameOrBeforeEvaluator.sameOrBefore(rightStart, leftEnd, precision, context)
                );
            }

            else {
                return AndEvaluator.and(
                        LessOrEqualEvaluator.lessOrEqual(leftStart, rightEnd, context),
                        LessOrEqualEvaluator.lessOrEqual(rightStart, leftEnd, context)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "Overlaps(Interval<T>, Interval<T>)",
                String.format("Overlaps(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return overlaps(left, right, precision, context);
    }
}
