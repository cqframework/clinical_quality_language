package org.opencds.cqf.cql.engine.elm.executing;

/*
    There are two overloads of this operator:
        T, List : The type of T must be the same as the element type of the list.
        T, Interval : The type of T must be the same as the point type of the interval.

    For the T, List overload, this operator returns if the given element is in the given list,
        and it is not the only element in the list, using equality semantics, with the exception
        that null elements are considered equal.
        If the first argument is null, the result is true if the list contains any null elements
        and at least one other element, and false otherwise.
        If the second argument is null, the result is false.

    For the T, Interval overload, this operator returns true if the given point is greater than
        the starting point, and less than the ending point of the interval, as determined by the Start and End operators.
        If precision is specified and the point type is a Date, DateTime, or Time type, comparisons used in the operation
        are performed at the specified precision.
        If the first argument is null, the result is null.
        If the second argument is null the result is false.
*/

import org.opencds.cqf.cql.engine.execution.State;

public class ProperInEvaluator {

    public static Boolean properIn(Object left, Object right, State state) {
        return ProperContainsEvaluator.properContains(right, left, state);
    }

    public static Boolean properIn(Object left, Object right, String precision, State state) {
        return ProperContainsEvaluator.properContains(right, left, precision, state);
    }

    public static Object internalEvaluate(Object left, Object right, String precision, State state) {

        if (precision != null) {
            return properIn(left, right, precision, state);
        }

        return properIn(left, right, state);
    }
}
