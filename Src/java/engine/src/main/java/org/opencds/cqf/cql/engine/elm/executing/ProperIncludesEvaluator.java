package org.opencds.cqf.cql.engine.elm.executing;

import java.util.stream.StreamSupport;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
*** NOTES FOR INTERVAL ***
properly includes _precision_ (left Interval<T>, right Interval<T>) Boolean

The properly includes operator for intervals returns true if the first interval completely includes the second
    and the first interval is strictly larger than the second. More precisely, if the starting point of the first interval
    is less than or equal to the starting point of the second interval, and the ending point of the first interval is
    greater than or equal to the ending point of the second interval, and they are not the same interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
properly includes(left List<T>, right List<T>) Boolean

The properly includes operator for lists returns true if the first list contains every element of the second list, a
    nd the first list is strictly larger than the second list.
This operator uses the notion of equivalence to determine whether or not two elements are the same.
If either argument is null, the result is null.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/

public class ProperIncludesEvaluator {

    public static Boolean properlyIncludes(Object left, Object right, String precision, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval) {
            return intervalProperlyIncludes((Interval) left, (Interval) right, precision, state);
        }
        if (left instanceof Iterable) {
            return listProperlyIncludes((Iterable<?>) left, (Iterable<?>) right, state);
        }

        throw new InvalidOperatorArgument(
                "ProperlyIncludes(Interval<T>, Interval<T>) or ProperlyIncludes(List<T>, List<T>)",
                String.format(
                        "ProperlyIncludes(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }

    public static Boolean intervalProperlyIncludes(Interval left, Interval right, String precision, State state) {
        Object leftStart = left.getStart();
        Object leftEnd = left.getEnd();
        Object rightStart = right.getStart();
        Object rightEnd = right.getEnd();

        if (leftStart instanceof BaseTemporal
                || leftEnd instanceof BaseTemporal
                || rightStart instanceof BaseTemporal
                || rightEnd instanceof BaseTemporal) {
            Boolean isSame = AndEvaluator.and(
                    SameAsEvaluator.sameAs(leftStart, rightStart, precision, state),
                    SameAsEvaluator.sameAs(leftEnd, rightEnd, precision, state));
            return AndEvaluator.and(
                    IncludedInEvaluator.intervalIncludedIn(right, left, precision, state),
                    isSame == null ? null : !isSame);
        }
        return AndEvaluator.and(
                IncludedInEvaluator.intervalIncludedIn(right, left, precision, state),
                NotEqualEvaluator.notEqual(left, right, state));
    }

    public static Boolean listProperlyIncludes(Iterable<?> left, Iterable<?> right, State state) {
        int leftCount = (int)
                StreamSupport.stream(((Iterable<?>) left).spliterator(), false).count();

        if (right == null) {
            return leftCount > 0;
        }

        return AndEvaluator.and(
                IncludedInEvaluator.listIncludedIn(right, left, state),
                GreaterEvaluator.greater(
                        leftCount,
                        (int) StreamSupport.stream(((Iterable<?>) right).spliterator(), false)
                                .count(),
                        state));
    }
}
