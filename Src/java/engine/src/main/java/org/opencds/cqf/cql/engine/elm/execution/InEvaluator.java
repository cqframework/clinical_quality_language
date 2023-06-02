package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Arrays;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Interval;

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

public class InEvaluator extends org.cqframework.cql.elm.execution.In
{
    public static Boolean in(Object left, Object right, String precision, Context context)
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
            return listIn(left, (Iterable<?>) right, context);
        }

        else if (right instanceof Interval)
        {
            return intervalIn(left, (Interval) right, precision, context);
        }

        throw new InvalidOperatorArgument(
                "In(T, Interval<T>) or In(T, List<T>)",
                String.format("In(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    private static Boolean intervalIn(Object left, Interval right, String precision, Context context)
    {
        Object rightStart = right.getStart();
        Object rightEnd = right.getEnd();

        if (left instanceof BaseTemporal)
        {
            if (AnyTrueEvaluator.anyTrue(Arrays.asList(SameAsEvaluator.sameAs(left, right.getStart(), precision, context), SameAsEvaluator.sameAs(left, right.getEnd(), precision, context))))
            {
                return true;
            }
            else if (AnyTrueEvaluator.anyTrue(Arrays.asList(BeforeEvaluator.before(left, right.getStart(), precision, context), AfterEvaluator.after(left, right.getEnd(), precision, context))))
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
                pointSameOrAfterStart = SameOrAfterEvaluator.sameOrAfter(left, rightStart, precision, context);
            }

            Boolean pointSameOrBeforeEnd;
            if (rightEnd == null && right.getHighClosed())
            {
                pointSameOrBeforeEnd = true;
            }
            else
            {
                pointSameOrBeforeEnd = SameOrBeforeEvaluator.sameOrBefore(left, rightEnd, precision, context);
            }

            return AndEvaluator.and(pointSameOrAfterStart, pointSameOrBeforeEnd);
        }

        else if (AnyTrueEvaluator.anyTrue(Arrays.asList(EqualEvaluator.equal(left, right.getStart(), context), EqualEvaluator.equal(left, right.getEnd(), context))))
        {
            return true;
        }
        else if (AnyTrueEvaluator.anyTrue(Arrays.asList(LessEvaluator.less(left, right.getStart(), context), GreaterEvaluator.greater(left, right.getEnd(), context))))
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
            greaterOrEqual = GreaterOrEqualEvaluator.greaterOrEqual(left, rightStart, context);
        }

        Boolean lessOrEqual;
        if (rightEnd == null && right.getHighClosed())
        {
            lessOrEqual = true;
        }
        else
        {
            lessOrEqual = LessOrEqualEvaluator.lessOrEqual(left, rightEnd, context);
        }

        return AndEvaluator.and(greaterOrEqual, lessOrEqual);
    }

    private static Boolean listIn(Object left, Iterable<?> right, Context context)
    {
        Boolean isEqual;
        for (Object element : right)
        {
            isEqual = EqualEvaluator.equal(left, element, context);
            if ((isEqual != null && isEqual))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Object internalEvaluate(Context context)
    {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        // null right operand case
//        if (getOperand().get(1) instanceof AsEvaluator) {
//            if (((AsEvaluator) getOperand().get(1)).getAsTypeSpecifier() instanceof IntervalTypeSpecifier) {
//                return intervalIn(left, (Interval) right, precision);
//            }
//            else {
//                return listIn(left, (Iterable) right);
//            }
//        }

        return in(left, right, precision, context);
    }
}
