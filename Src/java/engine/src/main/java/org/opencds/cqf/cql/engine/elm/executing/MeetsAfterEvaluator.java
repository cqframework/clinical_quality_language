package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
meets after _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets after operator returns true if the first interval starts immediately after the second interval ends.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class MeetsAfterEvaluator {

    public static Boolean meetsAfter(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Boolean isRightStartGreater = GreaterEvaluator.greater(((Interval) right).getStart(), ((Interval) left).getEnd(), state);
            if (isRightStartGreater != null && isRightStartGreater) {
                return false;
            }

            Object leftStart = ((Interval) left).getStart();
            Object rightEnd = ((Interval) right).getEnd();

            Boolean isIn = InEvaluator.in(((Interval) left).getEnd(), right, precision, state);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(leftStart, right, precision, state);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(rightEnd, left, precision, state);
            if (isIn != null && isIn) {
                return false;
            }

            return MeetsEvaluator.meetsOperation(rightEnd, leftStart, precision, state);
        }

        throw new InvalidOperatorArgument(
                "MeetsAfter(Interval<T>, Interval<T>)",
                String.format("MeetsAfter(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
