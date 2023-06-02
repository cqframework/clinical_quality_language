package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Time;

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

public class MeetsEvaluator extends org.cqframework.cql.elm.execution.Meets {

    public static Boolean meetsOperation(Object left, Object right, String precision, Context context) {
        if (left == null && right == null) {
            return null;
        }

        Object maxValue = MaxValueEvaluator.maxValue(left != null ? left.getClass().getName() : right.getClass().getName());
        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            Boolean isMax = SameAsEvaluator.sameAs(left, maxValue, precision, context);
            if (isMax != null && isMax) {
                return false;
            }

            String tempPrecision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
            if (precision == null && ((BaseTemporal) left).isUncertain(Precision.fromString(tempPrecision))) {
                return SameAsEvaluator.sameAs(SuccessorEvaluator.successor(left), right, tempPrecision, context);
            }
            else if (precision != null && ((BaseTemporal) left).isUncertain(Precision.fromString(precision))) {
                return SameAsEvaluator.sameAs(left, right, precision, context);
            }

            if (precision == null) {
                precision = tempPrecision;
            }

            //the following blocks adds 1 with the left and check if it is same as right when both params are of type DateTime/Time
            if (left instanceof DateTime && right instanceof DateTime) {
                DateTime dt = new DateTime(((DateTime) left).getDateTime().plus(1, Precision.fromString(precision).toChronoUnit()), ((BaseTemporal) left).getPrecision());
                return SameAsEvaluator.sameAs(dt, right, precision, context);
            }
            else if (left instanceof Time) {
                Time t = new Time(((Time) left).getTime().plus(1, Precision.fromString(precision).toChronoUnit()), ((BaseTemporal) left).getPrecision());
                return SameAsEvaluator.sameAs(t, right, precision, context);
            }
        }

        Boolean isMax = EqualEvaluator.equal(left, maxValue, context);
        if (isMax != null && isMax) {
            return false;
        }
        //the following gets the successor of left and check with Equal for params Date
        return EqualEvaluator.equal(SuccessorEvaluator.successor(left), right, context);
    }

    public static Boolean meets(Object left, Object right, String precision, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object leftEnd = ((Interval) left).getEnd();

            Boolean in = InEvaluator.in(leftStart, right, precision, context);
            if (in != null && in) {
                return false;
            }
            in = InEvaluator.in(leftEnd, right, precision, context);
            if (in != null && in) {
                return false;
            }

            return OrEvaluator.or(
                        MeetsBeforeEvaluator.meetsBefore(left, right, precision, context),
                        MeetsAfterEvaluator.meetsAfter(left, right, precision, context)
            );
        }

        throw new InvalidOperatorArgument(
                "Meets(Interval<T>, Interval<T>)",
                String.format("Meets(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return meets(left, right, precision, context);
    }
}
