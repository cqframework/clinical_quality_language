package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
width of(argument Interval<T>) T

The width operator returns the width of an interval.
The result of this operator is equivalent to invoking: (start of argument – end of argument) + point-size.
Note that because CQL defines duration and difference operations for date/time and time valued intervals,
  width is not defined for intervals of these types.
If the argument is null, the result is null.
*/

public class WidthEvaluator extends org.cqframework.cql.elm.execution.Width {

    public static Object width(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Interval) {
            Object start = ((Interval) operand).getStart();
            Object end = ((Interval) operand).getEnd();

            return Interval.getSize(start, end);
        }

        throw new InvalidOperatorArgument(
                "Width(Interval<T>)",
                String.format("Width(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);

        return width(operand);
    }
}
