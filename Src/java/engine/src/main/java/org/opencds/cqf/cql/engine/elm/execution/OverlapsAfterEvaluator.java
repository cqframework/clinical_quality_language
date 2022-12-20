package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
overlaps after _precision_ (left Interval<T>, right Interval<T>) Boolean

The overlaps after operator returns true if the first interval overlaps the second and ends after it.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class OverlapsAfterEvaluator extends org.cqframework.cql.elm.execution.OverlapsAfter {

    public static Object overlapsAfter(Object left, Object right, String precision, Context context) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftEnd = ((Interval) left).getEnd();
            Object rightEnd = ((Interval) right).getEnd();

            if (leftEnd instanceof BaseTemporal && rightEnd instanceof BaseTemporal) {
                return AndEvaluator.and(
                        AfterEvaluator.after(leftEnd, rightEnd, precision, context),
                        OverlapsEvaluator.overlaps(left, right, precision, context)
                );
            }

            else {
                return AndEvaluator.and(
                        GreaterEvaluator.greater(leftEnd, rightEnd, context),
                        OverlapsEvaluator.overlaps(left, right, precision, context)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "OverlapsAfter(Interval<T>, Interval<T>)",
                String.format("Overlaps(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return overlapsAfter(left, right, precision, context);
    }
}
