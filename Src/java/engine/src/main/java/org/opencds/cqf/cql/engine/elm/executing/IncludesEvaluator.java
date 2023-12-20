package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
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

public class IncludesEvaluator {

    public static Boolean includes(Object left, Object right, String precision, State state) {
        try {
            return IncludedInEvaluator.includedIn(right, left, precision, state);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperatorArgument(
                    "Includes(Interval<T>, Interval<T>), Includes(List<T>, List<T>) or Includes(List<T>, T)",
                    String.format(
                            "Includes(%s, %s)",
                            left.getClass().getName(), right.getClass().getName()));
        }
    }

    public static Object internalEvaluate(Object left, Object right, String precision, State state) {

        if (left == null && right == null) {
            return null;
        }

        if (left == null) {
            return right instanceof Interval
                    ? IncludedInEvaluator.intervalIncludedIn((Interval) right, null, precision, state)
                    : IncludedInEvaluator.listIncludedIn((Iterable<?>) right, null, state);
        }

        if (right == null) {
            return left instanceof Interval
                    ? IncludedInEvaluator.intervalIncludedIn(null, (Interval) left, precision, state)
                    : IncludedInEvaluator.listIncludedIn(null, (Iterable<?>) left, state);
        }

        return includes(left, right, precision, state);
    }
}
