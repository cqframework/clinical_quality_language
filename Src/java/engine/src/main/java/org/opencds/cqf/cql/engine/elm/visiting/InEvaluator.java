package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

import java.util.Arrays;

/*
*** NOTES FOR INTERVAL ***
in(point T, argument Interval<T>) Boolean

The in operator for intervals returns true if the given point is greater than or equal to the
    starting point of the interval, and less than or equal to the ending point of the interval.
    For open interval boundaries, exclusive comparison operators are used.
    For closed interval boundaries, if the interval boundary is null, the result of the boundary comparison is considered true.
If precision is specified and the point type is a date/time type, comparisons used in the
    operation are performed at the specified precision.
If either argument is null, the result is null.

*/

/*
*** NOTES FOR LIST ***
in(element T, argument List<T>) Boolean

The in operator for lists returns true if the given element is in the given list using equality semantics.

If either argument is null, the result is null.

*/

public class InEvaluator
{
    public static Boolean in(Object left, Object right, String precision, State state)
    {
        if (left == null )
        {
            return null;
        }

        if(right == null)
        {
            return false;
        }

        if (right instanceof Iterable)
        {
            return listIn(left, (Iterable<?>) right, state);
        }

        else if (right instanceof Interval)
        {
            return intervalIn(left, (Interval) right, precision, state);
        }

        throw new InvalidOperatorArgument(
                "In(T, Interval<T>) or In(T, List<T>)",
                String.format("In(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    private static Boolean intervalIn(Object left, Interval right, String precision, State state)
    {
        Object rightStart = right.getStart();
        Object rightEnd = right.getEnd();

        if (left instanceof BaseTemporal)
        {
            if (AnyTrueEvaluator.anyTrue(Arrays.asList(SameAsEvaluator.sameAs(left, right.getStart(), precision, state), SameAsEvaluator.sameAs(left, right.getEnd(), precision, state))))
            {
                return true;
            }
            else if (AnyTrueEvaluator.anyTrue(Arrays.asList(BeforeEvaluator.before(left, right.getStart(), precision, state), AfterEvaluator.after(left, right.getEnd(), precision, state))))
            {
                return false;
            }

            Boolean pointSameOrAfterStart;
            if (rightStart == null && right.getLowClosed())
            {
                pointSameOrAfterStart = true;
            }
            else
            {
                pointSameOrAfterStart = SameOrAfterEvaluator.sameOrAfter(left, rightStart, precision, state);
            }

            Boolean pointSameOrBeforeEnd;
            if (rightEnd == null && right.getHighClosed())
            {
                pointSameOrBeforeEnd = true;
            }
            else
            {
                pointSameOrBeforeEnd = SameOrBeforeEvaluator.sameOrBefore(left, rightEnd, precision, state);
            }

            return AndEvaluator.and(pointSameOrAfterStart, pointSameOrBeforeEnd);
        }

        else if (AnyTrueEvaluator.anyTrue(Arrays.asList(EqualEvaluator.equal(left, right.getStart(), state), EqualEvaluator.equal(left, right.getEnd(), state))))
        {
            return true;
        }
        else if (AnyTrueEvaluator.anyTrue(Arrays.asList(LessEvaluator.less(left, right.getStart(), state), GreaterEvaluator.greater(left, right.getEnd(), state))))
        {
            return false;
        }

        Boolean greaterOrEqual;
        if (rightStart == null && right.getLowClosed())
        {
            greaterOrEqual = true;
        }
        else
        {
            greaterOrEqual = GreaterOrEqualEvaluator.greaterOrEqual(left, rightStart, state);
        }

        Boolean lessOrEqual;
        if (rightEnd == null && right.getHighClosed())
        {
            lessOrEqual = true;
        }
        else
        {
            lessOrEqual = LessOrEqualEvaluator.lessOrEqual(left, rightEnd, state);
        }

        return AndEvaluator.and(greaterOrEqual, lessOrEqual);
    }

    private static Boolean listIn(Object left, Iterable<?> right, State state)
    {
        Boolean isEqual;
        for (Object element : right)
        {
            isEqual = EqualEvaluator.equal(left, element, state);
            if ((isEqual != null && isEqual))
            {
                return true;
            }
        }

        return false;
    }

    public  static Object internalEvaluate(Object left , Object right, String precision, State state)
    {

        // null right operand case
//        if (getOperand().get(1) instanceof AsEvaluator) {
//            if (((AsEvaluator) getOperand().get(1)).getAsTypeSpecifier() instanceof IntervalTypeSpecifier) {
//                return intervalIn(left, (Interval) right, precision);
//            }
//            else {
//                return listIn(left, (Iterable) right);
//            }
//        }

        return in(left, right, precision, state);
    }
}
