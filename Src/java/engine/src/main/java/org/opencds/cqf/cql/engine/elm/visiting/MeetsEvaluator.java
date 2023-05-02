package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.*;

/*
meets _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets operator returns true if the first interval ends immediately before the second interval starts,
    or if the first interval starts immediately after the second interval ends.
    In other words, if the ending point of the first interval is equal to the predecessor of the starting point of the second,
    or if the starting point of the first interval is equal to the successor of the ending point of the second.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class MeetsEvaluator {

    public static Boolean meetsOperation(Object left, Object right, String precision, State state) {
        if (left == null && right == null) {
            return null;
        }

        Object maxValue = MaxValueEvaluator.maxValue(left != null ? left.getClass().getName() : right.getClass().getName());
        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            Boolean isMax = SameAsEvaluator.sameAs(left, maxValue, precision, state);
            if (isMax != null && isMax) {
                return false;
            }

            String tempPrecision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
            if (precision == null && ((BaseTemporal) left).isUncertain(Precision.fromString(tempPrecision))) {
                return SameAsEvaluator.sameAs(SuccessorEvaluator.successor(left), right, tempPrecision, state);
            }
            else if (precision != null && ((BaseTemporal) left).isUncertain(Precision.fromString(precision))) {
                return SameAsEvaluator.sameAs(left, right, precision, state);
            }

            if (precision == null) {
                precision = tempPrecision;
            }

            //the following blocks adds 1 with the left and check if it is same as right when both params are of type DateTime/Time
            if (left instanceof DateTime && right instanceof DateTime) {
                DateTime dt = new DateTime(((DateTime) left).getDateTime().plus(1, Precision.fromString(precision).toChronoUnit()), ((BaseTemporal) left).getPrecision());
                return SameAsEvaluator.sameAs(dt, right, precision, state);
            }
            else if (left instanceof Time) {
                Time t = new Time(((Time) left).getTime().plus(1, Precision.fromString(precision).toChronoUnit()), ((BaseTemporal) left).getPrecision());
                return SameAsEvaluator.sameAs(t, right, precision, state);
            }
        }

        Boolean isMax = EqualEvaluator.equal(left, maxValue, state);
        if (isMax != null && isMax) {
            return false;
        }
        //the following gets the successor of left and check with Equal for params Date
        return EqualEvaluator.equal(SuccessorEvaluator.successor(left), right, state);
    }

    public static Boolean meets(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object leftEnd = ((Interval) left).getEnd();

            Boolean in = InEvaluator.in(leftStart, right, precision, state);
            if (in != null && in) {
                return false;
            }
            in = InEvaluator.in(leftEnd, right, precision, state);
            if (in != null && in) {
                return false;
            }

            return OrEvaluator.or(
                        MeetsBeforeEvaluator.meetsBefore(left, right, precision, state),
                        MeetsAfterEvaluator.meetsAfter(left, right, precision, state)
            );
        }

        throw new InvalidOperatorArgument(
                "Meets(Interval<T>, Interval<T>)",
                String.format("Meets(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
