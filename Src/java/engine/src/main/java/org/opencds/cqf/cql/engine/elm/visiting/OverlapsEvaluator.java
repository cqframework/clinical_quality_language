package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
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

public class OverlapsEvaluator {

    public static Boolean overlaps(Object left, Object right, String precision, State state) {
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
                        SameOrBeforeEvaluator.sameOrBefore(leftStart, rightEnd, precision, state),
                        SameOrBeforeEvaluator.sameOrBefore(rightStart, leftEnd, precision, state)
                );
            }

            else {
                return AndEvaluator.and(
                        LessOrEqualEvaluator.lessOrEqual(leftStart, rightEnd, state),
                        LessOrEqualEvaluator.lessOrEqual(rightStart, leftEnd, state)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "Overlaps(Interval<T>, Interval<T>)",
                String.format("Overlaps(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
