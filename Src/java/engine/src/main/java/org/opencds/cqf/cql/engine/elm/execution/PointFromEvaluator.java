package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidInterval;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
point from(argument Interval<T>) : T
The point from operator extracts the single point from a unit interval. If the argument is not a unit interval, a run-time error is thrown.

If the argument is null, the result is null.
* */
public class PointFromEvaluator extends org.cqframework.cql.elm.execution.PointFrom {

    public static Object pointFrom(Object operand, Context context) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Interval) {
            Object start = ((Interval) operand).getStart();
            Object end = ((Interval) operand).getEnd();

            Boolean equal = EqualEvaluator.equal(start, end, context);
            if (equal != null && equal) {
                return start;
            }

            throw new InvalidInterval("Cannot perform PointFrom operation on intervals that are not unit intervals.");
        }

        throw new InvalidOperatorArgument(
                "PointFrom(Interval<T>)",
                String.format("PointFrom(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return pointFrom(operand, context);
    }
}
