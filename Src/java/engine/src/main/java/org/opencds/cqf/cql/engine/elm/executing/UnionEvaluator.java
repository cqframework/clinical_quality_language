package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

/*
*** NOTES FOR INTERVAL ***
union(left Interval<T>, right Interval<T>) Interval<T>

The union operator for intervals returns the union of the intervals.
  More precisely, the operator returns the interval that starts at the earliest starting point in either argument,
    and ends at the latest starting point in either argument.
If the arguments do not overlap or meet, this operator returns null.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
union(left List<T>, right List<T>) List<T>

The union operator for lists returns a list with all elements from both arguments.
    Note that duplicates are eliminated during this process; if an element appears in both sources,
    that element will only appear once in the resulting list.
If either argument is null, the result is null.
Note that the union operator can also be invoked with the symbolic operator (|).
*/

public class UnionEvaluator {

    public static Interval unionInterval(Interval left, Interval right, State state) {
        if (left == null || right == null) {
            return null;
        }

        Object leftStart = left.getStart();
        Object leftEnd = left.getEnd();
        Object rightStart = right.getStart();
        Object rightEnd = right.getEnd();

        if (leftStart == null || leftEnd == null || rightStart == null || rightEnd == null) {
            return null;
        }

        String precision = null;
        if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal) {
            precision = BaseTemporal.getHighestPrecision(
                    (BaseTemporal) leftStart, (BaseTemporal) leftEnd, (BaseTemporal) rightStart, (BaseTemporal)
                            rightEnd);
        }

        Boolean overlapsOrMeets = OrEvaluator.or(
                OverlapsEvaluator.overlaps(left, right, precision, state),
                MeetsEvaluator.meets(left, right, precision, state));
        if (overlapsOrMeets == null || !overlapsOrMeets) {
            return null;
        }

        Object min = LessEvaluator.less(leftStart, rightStart, state) ? leftStart : rightStart;
        Object max = GreaterEvaluator.greater(leftEnd, rightEnd, state) ? leftEnd : rightEnd;

        return new Interval(min, true, max, true);
    }

    public static Iterable<?> unionIterable(Iterable<?> left, Iterable<?> right, State state) {
        if (left == null && right == null) {
            return Collections.emptyList();
        }

        if (left == null) {
            return DistinctEvaluator.distinct(right, state);
        }

        if (right == null) {
            return DistinctEvaluator.distinct(left, state);
        }

        // List Logic
        List<Object> result = new ArrayList<>();
        for (Object leftElement : left) {
            result.add(leftElement);
        }

        for (Object rightElement : right) {
            result.add(rightElement);
        }
        return DistinctEvaluator.distinct(result, state);
    }

    public static Object union(Object left, Object right, State state) {
        if (left instanceof Interval || right instanceof Interval) {
            return unionInterval((Interval) left, (Interval) right, state);
        } else if (left instanceof Iterable || right instanceof Iterable) {
            return unionIterable((Iterable<?>) left, (Iterable<?>) right, state);
        }

        var leftName = left != null ? left.getClass().getName() : "<unknown>";
        var rightName = right != null ? right.getClass().getName() : "<unknown>";

        throw new InvalidOperatorArgument(
                "Union(Interval<T>, Interval<T>) or Union(List<T>, List<T>)",
                String.format("Union(%s, %s)", leftName, rightName));
    }
}
