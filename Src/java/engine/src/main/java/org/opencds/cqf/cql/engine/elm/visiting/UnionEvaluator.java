package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

import java.util.ArrayList;
import java.util.List;

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

    public static Object union(Object left, Object right, State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            Object leftStart = ((Interval) left).getStart();
            Object leftEnd = ((Interval) left).getEnd();
            Object rightStart = ((Interval) right).getStart();
            Object rightEnd = ((Interval) right).getEnd();

            if (leftStart == null || leftEnd == null
                    || rightStart == null || rightEnd == null)
            {
                return null;
            }

            String precision = null;
            if (leftStart instanceof BaseTemporal && rightStart instanceof BaseTemporal) {
                precision = BaseTemporal.getHighestPrecision((BaseTemporal) leftStart, (BaseTemporal) leftEnd, (BaseTemporal) rightStart, (BaseTemporal) rightEnd);
            }

            Boolean overlapsOrMeets = OrEvaluator.or(
                    OverlapsEvaluator.overlaps(left, right, precision, state),
                    MeetsEvaluator.meets(left, right, precision, state)
            );
            if (overlapsOrMeets == null || !overlapsOrMeets) {
                return null;
            }

            Object min = LessEvaluator.less(leftStart, rightStart, state) ? leftStart : rightStart;
            Object max = GreaterEvaluator.greater(leftEnd, rightEnd, state) ? leftEnd : rightEnd;

            return new Interval(min, true, max, true);
        }

        else if (left instanceof Iterable) {
            // List Logic
            List<Object> result = new ArrayList<>();
            for (Object leftElement : (Iterable<?>)left) {
                result.add(leftElement);
            }

            for (Object rightElement : (Iterable<?>)right) {
                result.add(rightElement);
            }
            return DistinctEvaluator.distinct(result, state);
        }

        throw new InvalidOperatorArgument(
                "Union(Interval<T>, Interval<T>) or Union(List<T>, List<T>)",
                String.format("Union(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
