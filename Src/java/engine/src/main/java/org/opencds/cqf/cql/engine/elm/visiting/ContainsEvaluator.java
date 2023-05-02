package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.As;
import org.hl7.elm.r1.IntervalTypeSpecifier;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;

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

public class ContainsEvaluator {

    public static Object contains(Object left, Object right, String precision, State state) {
        try {
            return InEvaluator.in(right, left, precision, state);
        } catch (InvalidOperatorArgument e) {
            throw new InvalidOperatorArgument(
                    "Contains(List<T>, T)",
                    String.format("Contains(%s, %s)", left.getClass().getName(), right.getClass().getName())
            );
        }
    }

    public static Object internalEvaluate(Object left, Object right, Object expression, String precision, State state) {

        if(left == null && right != null){
            return false;
        }
        if ( right == null) {
            return null;
        }

        // null left operand case
        if (expression instanceof As) {
            if (((As) expression).getAsTypeSpecifier() instanceof IntervalTypeSpecifier) {
                return InEvaluator.in(right, left, precision, state);
            }
            else {
                return InEvaluator.in(right, left, null, state);
            }
        }

        return contains(left, right, precision, state);
    }
}
