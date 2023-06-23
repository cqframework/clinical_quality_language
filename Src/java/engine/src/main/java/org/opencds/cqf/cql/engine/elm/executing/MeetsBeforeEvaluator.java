package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
meets before _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets before operator returns true if the first interval ends immediately before the second interval starts.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class MeetsBeforeEvaluator {

    public static Boolean meetsBefore(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Boolean isLeftStartGreater = GreaterEvaluator.greater(((Interval) left).getStart(), ((Interval) right).getEnd(), state);
            if (isLeftStartGreater != null && isLeftStartGreater) {
                return false;
            }

            Object leftEnd = ((Interval) left).getEnd();
            Object rightStart = ((Interval) right).getStart();

            Boolean isIn = InEvaluator.in(leftEnd, right, precision, state);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(((Interval) left).getStart(), right, precision, state);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(leftEnd, right, precision, state);
            if (isIn != null && isIn) {
                return false;
            }

            return MeetsEvaluator.meetsOperation(leftEnd, rightStart, precision, state);
        }

        throw new InvalidOperatorArgument(
                "MeetsBefore(Interval<T>, Interval<T>)",
                String.format("MeetsBefore(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
