package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
*** NOTES FOR INTERVAL ***
includes _precision_ (left Interval<T>, right Interval<T>) Boolean

The includes operator for intervals returns true if the first interval completely includes the second.
    More precisely, if the starting point of the first interval is less than or equal to the starting point
    of the second interval, and the ending point of the first interval is greater than or equal to the ending point
    of the second interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
includes(left List<T>, right List<T>) Boolean
includes(left List<T>, right T) Boolean

The includes operator for lists returns true if the first list contains every element of the second list using equality semantics.
For the singleton overload, this operator returns true if the list includes (i.e. contains) the singleton.
If either argument is null, the result is null.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/

public class IncludesEvaluator extends org.cqframework.cql.elm.execution.Includes {

    public static Boolean includes(Object left, Object right, String precision, Context context) {
        try {
            return IncludedInEvaluator.includedIn(right, left, precision, context);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidOperatorArgument(
                    "Includes(Interval<T>, Interval<T>), Includes(List<T>, List<T>) or Includes(List<T>, T)",
                    String.format("Includes(%s, %s)", left.getClass().getName(), right.getClass().getName())
            );
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() != null ? getPrecision().value() : null;

        if (left == null && right == null) {
            return null;
        }

        if (left == null) {
            return right instanceof Interval
                    ? IncludedInEvaluator.intervalIncludedIn((Interval) right, null, precision, context)
                    : IncludedInEvaluator.listIncludedIn((Iterable<?>) right, null, context);
        }

        if (right == null) {
            return left instanceof Interval
                    ? IncludedInEvaluator.intervalIncludedIn(null, (Interval) left, precision, context)
                    : IncludedInEvaluator.listIncludedIn(null, (Iterable<?>) left, context);
        }

        return includes(left, right, precision, context);
    }
}
