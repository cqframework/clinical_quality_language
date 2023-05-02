package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

import java.util.ArrayList;
import java.util.List;

/*
*** NOTES FOR INTERVAL ***
intersect(left Interval<T>, right Interval<T>) Interval<T>

The intersect operator for intervals returns the intersection of two intervals.
  More precisely, the operator returns the interval that defines the overlapping portion of both arguments.
If the arguments do not overlap, this operator returns null.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
intersect(left List<T>, right List<T>) List<T>

The intersect operator for lists returns the intersection of two lists.
  More precisely, the operator returns a list containing only the elements that appear in both lists.
This operator uses equality semantics to determine whether or not two elements are the same.
The operator is defined with set semantics, meaning that each element will appear in the result at most once,
    and that there is no expectation that the order of the inputs will be preserved in the results.
If either argument is null, the result is null.
*/

public class IntersectEvaluator
{
    public static Object intersect(Object left, Object right, State state)
    {
        if (left == null || right == null)
        {
            return null;
        }

        if (left instanceof Interval)
        {
            Interval leftInterval = (Interval)left;
            Interval rightInterval = (Interval)right;

            Object leftStart = leftInterval.getStart();
            Object leftEnd = leftInterval.getEnd();
            Object rightStart = rightInterval.getStart();
            Object rightEnd = rightInterval.getEnd();

            if (leftStart == null || leftEnd == null
                    || rightStart == null || rightEnd == null)
            {
                return null;
            }

            String precision = null;
            if (leftStart instanceof BaseTemporal
                    && rightStart instanceof BaseTemporal)
            {
                precision = BaseTemporal.getHighestPrecision((BaseTemporal) leftStart, (BaseTemporal) leftEnd, (BaseTemporal) rightStart, (BaseTemporal) rightEnd);
            }

            Boolean overlaps = OverlapsEvaluator.overlaps(leftInterval, rightInterval, precision, state);
            if (overlaps == null || !overlaps)
            {
                return null;
            }

            Boolean leftStartGtRightStart = GreaterEvaluator.greater(leftStart, rightStart, state);
            Boolean leftEndLtRightEnd = LessEvaluator.less(leftEnd, rightEnd, state);

            Object max;
            if (leftStartGtRightStart == null && precision != null)
            {
                max = ((BaseTemporal) leftStart).getPrecision().toString().equals(precision) ? leftStart : rightStart;
            }
            else
            {
                max = leftStartGtRightStart == null ? null : leftStartGtRightStart ? leftStart : rightStart;
            }

            Object min;
            if (leftEndLtRightEnd == null && precision != null)
            {
                min = ((BaseTemporal) leftEnd).getPrecision().toString().equals(precision) ? leftEnd : rightEnd;
            }
            else
            {
                min = leftEndLtRightEnd == null ? null : leftEndLtRightEnd ? leftEnd : rightEnd;
            }

            return new Interval(max, max != null, min, min != null);
        }

        else if (left instanceof Iterable)
        {
            Iterable<?> leftArr = (Iterable<?>)left;
            Iterable<?> rightArr = (Iterable<?>)right;

            List<Object> result = new ArrayList<>();
            Boolean in;
            for (Object leftItem : leftArr)
            {
                in = InEvaluator.in(leftItem, rightArr, null, state);
                if (in != null && in)
                {
                    result.add(leftItem);
                }
            }

            return DistinctEvaluator.distinct(result, state);
        }

        throw new InvalidOperatorArgument(
                "Intersect(Interval<T>, Interval<T>) or Intersect(List<T>, List<T>)",
                String.format("Intersect(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
