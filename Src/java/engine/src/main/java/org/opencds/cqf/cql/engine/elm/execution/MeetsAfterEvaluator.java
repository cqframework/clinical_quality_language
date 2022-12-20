package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
meets after _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets after operator returns true if the first interval starts immediately after the second interval ends.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class MeetsAfterEvaluator extends org.cqframework.cql.elm.execution.MeetsAfter {

    public static Boolean meetsAfter(Object left, Object right, String precision, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Boolean isRightStartGreater = GreaterEvaluator.greater(((Interval) right).getStart(), ((Interval) left).getEnd(), context);
            if (isRightStartGreater != null && isRightStartGreater) {
                return false;
            }

            Object leftStart = ((Interval) left).getStart();
            Object rightEnd = ((Interval) right).getEnd();

            Boolean isIn = InEvaluator.in(((Interval) left).getEnd(), right, precision, context);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(leftStart, right, precision, context);
            if (isIn != null && isIn) {
                return false;
            }
            isIn = InEvaluator.in(rightEnd, left, precision, context);
            if (isIn != null && isIn) {
                return false;
            }

            return MeetsEvaluator.meetsOperation(rightEnd, leftStart, precision, context);
        }

        throw new InvalidOperatorArgument(
                "MeetsAfter(Interval<T>, Interval<T>)",
                String.format("MeetsAfter(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return meetsAfter(left, right, precision, context);
    }
}
