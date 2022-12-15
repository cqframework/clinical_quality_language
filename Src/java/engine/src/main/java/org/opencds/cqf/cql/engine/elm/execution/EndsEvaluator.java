package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
ends _precision_ (left Interval<T>, right Interval<T>) Boolean

The ends operator returns true if the first interval ends the second.
    More precisely, if the starting point of the first interval is greater than or equal to the starting point of the second,
    and the ending point of the first interval is equal to the ending point of the second.
This operator uses the semantics described in the start and end operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class EndsEvaluator extends org.cqframework.cql.elm.execution.Ends {

    public static Boolean ends(Object left, Object right, String precision, Context context) {
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
                        SameOrAfterEvaluator.sameOrAfter(leftStart, rightStart, precision, context),
                        SameAsEvaluator.sameAs(leftEnd, rightEnd, precision, context)
                );
            }

            else {
                return AndEvaluator.and(
                        GreaterOrEqualEvaluator.greaterOrEqual(leftStart, rightStart, context),
                        EqualEvaluator.equal(leftEnd, rightEnd, context)
                );
            }
        }

        throw new InvalidOperatorArgument(
                "Ends(Interval<T>, Interval<T>)",
                String.format("Ends(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return ends(left, right, precision, context);
    }
}
