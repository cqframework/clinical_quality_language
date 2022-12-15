package org.opencds.cqf.cql.engine.elm.execution;

import org.cqframework.cql.elm.execution.IntervalTypeSpecifier;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
contains(argument List<T>, element T) Boolean

The contains operator for lists returns true if the given element is in the list.
This operator uses the notion of equivalence to determine whether or not the element being searched for is equivalent to any element in the list.
    In particular this means that if the list contains a null, and the element being searched for is null, the result will be true.
If the list argument is null, the result is false.

contains _precision_ (argument Interval<T>, point T) Boolean
The contains operator for intervals returns true if the given point is greater than or equal to the starting point of the interval,
    and less than or equal to the ending point of the interval. For open interval boundaries, exclusive comparison operators are used.
    For closed interval boundaries, if the interval boundary is null, the result of the boundary comparison is considered true.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/

public class ContainsEvaluator extends org.cqframework.cql.elm.execution.Contains {

    public static Object contains(Object left, Object right, String precision, Context context) {
        try {
            return InEvaluator.in(right, left, precision, context);
        } catch (InvalidOperatorArgument e) {
            throw new InvalidOperatorArgument(
                    "Contains(List<T>, T)",
                    String.format("Contains(%s, %s)", left.getClass().getName(), right.getClass().getName())
            );
        }
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        if(left == null && right != null){
            return false;
        }
        if ( right == null) {
            return null;
        }

        // null left operand case
        if (getOperand().get(0) instanceof AsEvaluator) {
            if (((AsEvaluator) getOperand().get(0)).getAsTypeSpecifier() instanceof IntervalTypeSpecifier) {
                return InEvaluator.in(right, left, precision, context);
            }
            else {
                return InEvaluator.in(right, left, null, context);
            }
        }

        return contains(left, right, precision, context);
    }
}
