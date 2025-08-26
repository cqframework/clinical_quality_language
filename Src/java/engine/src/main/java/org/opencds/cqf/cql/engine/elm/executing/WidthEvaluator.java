package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
width of(argument Interval<T>) T

The width operator returns the width of an interval.
The result of this operator is equivalent to invoking: (start of argument – end of argument) + point-size.
Note that because CQL defines duration and difference operations for date/time and time valued intervals,
  width is not defined for intervals of these types.
If the argument is null, the result is null.
*/

public class WidthEvaluator {

    public static Object width(Object operand, final State state) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Interval interval) {
            Object start = interval.getStart();
            Object end = interval.getEnd();

            return Interval.getSize(start, end, state);
        }

        throw new InvalidOperatorArgument(
                "Width(Interval<T>)",
                String.format("Width(%s)", operand.getClass().getName()));
    }
}
