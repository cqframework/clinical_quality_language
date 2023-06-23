package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
start of(argument Interval<T>) T

The Start operator returns the starting point of an interval.
If the low boundary of the interval is open, this operator returns the successor of the low value of the interval.
  Note that if the low value of the interval is null, the result is null.
If the low boundary of the interval is closed and the low value of the interval is not null, this operator returns the
  low value of the interval. Otherwise, the result is the minimum value of the point type of the interval.
If the argument is null, the result is null.
*/

public class StartEvaluator {

    public static Object start(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Interval) {
            return ((Interval) operand).getStart();
        }

        throw new InvalidOperatorArgument(
                "Start(Interval<T>)",
                String.format("Start(%s)", operand.getClass().getName())
        );
    }

}
