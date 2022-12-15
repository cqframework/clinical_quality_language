package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
    Size(argument Interval<T>) T

    The Size operator returns the size of an interval. The result of this operator is equivalent to invoking:
        (end of argument – start of argument) + point-size, where point-size is determined by successor of minimum T - minimum T.

    Note that because CQL defines duration and difference operations for date and time valued intervals, size is not defined for intervals of these types.

    If the argument is null, the result is null.

    The following examples illustrate the behavior of the Size operator:

    define SizeTest: Size(Interval[3, 7]) // 5, i.e. the interval contains 5 points
    define SizeTestEquivalent: Size(Interval[3, 8)) // 5, i.e. the interval contains 5 points
    define SizeIsNull: Size(null as Interval<Integer>) // null

*/

public class SizeEvaluator extends org.cqframework.cql.elm.execution.Size {

    public static Object size(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Interval) {
            return SubtractEvaluator.subtract(
                    SuccessorEvaluator.successor(((Interval) argument).getEnd()),
                    ((Interval) argument).getStart()
            );
        }

        throw new InvalidOperatorArgument(
                "Size(Interval<T>)",
                String.format("Size(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object argument = getOperand().evaluate(context);
        return size(argument);
    }

}
