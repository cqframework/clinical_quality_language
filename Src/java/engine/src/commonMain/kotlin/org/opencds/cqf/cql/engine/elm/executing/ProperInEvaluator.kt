package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

/*
   There are two overloads of this operator:
       T, List : The type of T must be the same as the element type of the list.
       T, Interval : The type of T must be the same as the point type of the interval.

   For the T, List overload, this operator returns if the given element is in the given list,
       and it is not the only element in the list, using equivalence semantics.
       If the list-valued argument is null, it should be treated as an empty list.

   For the T, Interval overload, this operator returns true if the given point is greater than
       the starting point, and less than the ending point of the interval, as determined by the Start and End operators.
       If precision is specified and the point type is a date/time type, comparisons used in the operation are performed
           at the specified precision.
*/

object ProperInEvaluator {
    fun properIn(left: Any?, right: Any?, state: State?): Boolean? {
        return ProperContainsEvaluator.properContains(right, left, state)
    }

    fun properIn(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        return ProperContainsEvaluator.properContains(right, left, precision, state)
    }

    @JvmStatic
    fun internalEvaluate(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (precision != null) {
            return properIn(left, right, precision, state)
        }

        return properIn(left, right, state)
    }
}
