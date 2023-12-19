package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
*** NOTES FOR INTERVAL ***
properly included in(left Interval<T>, right Interval<T>) Boolean

The properly included in operator for intervals returns true if the first interval is completely included in the second and
  the first interval is strictly smaller than the second.
  More precisely, if the starting point of the first interval is greater than or equal to the starting point of the second interval,
    and the ending point of the first interval is less than or equal to the ending point of the second interval,
      and they are not the same interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If either argument is null, the result is null.
Note that during is a synonym for included in.

*** NOTES FOR LIST ***
properly included in(left List<T>, right list<T>) Boolean

The properly included in operator for lists returns true if every element of the first list is in the second list and the
    first list is strictly smaller than the second list.
This operator uses the notion of equivalence to determine whether or not two elements are the same.
If the left argument is null, the result is true if the right argument is not empty. Otherwise, if the right argument is null, the result is false.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/

public class ProperIncludedInEvaluator {

    public static Object properlyIncludedIn(Object left, Object right, String precision, State state) {
        if (left == null && right == null) {
            return null;
        }

        try {
            if (left == null) {
                return right instanceof Interval
                        ? ProperIncludesEvaluator.intervalProperlyIncludes((Interval) right, null, precision, state)
                        : ProperIncludesEvaluator.listProperlyIncludes((Iterable<?>) right, null, state);
            }

            if (right == null) {
                return left instanceof Interval
                        ? ProperIncludesEvaluator.intervalProperlyIncludes(null, (Interval) left, precision, state)
                        : ProperIncludesEvaluator.listProperlyIncludes(null, (Iterable<?>) left, state);
            }

            return ProperIncludesEvaluator.properlyIncludes(right, left, precision, state);
        } catch (InvalidOperatorArgument e) {
            throw new InvalidOperatorArgument(
                    "ProperIncludedIn(Interval<T>, Interval<T>) or ProperIncludedIn(List<T>, List<T>)",
                    String.format(
                            "ProperlyIncludedIn(%s, %s)",
                            left.getClass().getName(), right.getClass().getName()));
        }
    }
}
