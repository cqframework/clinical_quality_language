package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
overlaps before _precision_ (left Interval<T>, right Interval<T>) Boolean

The operator overlaps before returns true if the first interval overlaps the second and starts before it.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class OverlapsBeforeEvaluator {

    public static Object overlapsBefore(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object rightStart = ((Interval) right).getStart();

            if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal) {
                return AndEvaluator.and(
                        BeforeEvaluator.before(leftStart, rightStart, precision, state),
                        OverlapsEvaluator.overlaps(left, right, precision, state)
                );
            }

            else {
                return AndEvaluator.and(
                        LessEvaluator.less(leftStart, rightStart, state),
                        OverlapsEvaluator.overlaps(left, right, precision, state)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "OverlapsBefore(Interval<T>, Interval<T>)",
                String.format("OverlapsBefore(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }
}
