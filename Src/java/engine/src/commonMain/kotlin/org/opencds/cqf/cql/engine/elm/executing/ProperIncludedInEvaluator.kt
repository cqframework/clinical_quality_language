package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
properly included in(left Interval<T>, right Interval<T>) Boolean

The properly included in operator for intervals returns true if the first interval is completely included in the second and
  the first interval is strictly smaller than the second.
  More precisely, if the starting point of the first interval is greater than or equal to the starting point of the second interval,
    and the ending point of the first interval is less than or equal to the ending point of the second interval,
      and they are not the same interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If either argument is null, the result is null.
Note that during is a synonym for included in.

*** NOTES FOR LIST ***
properly included in(left List<T>, right list<T>) Boolean

The properly included in operator for lists returns true if every element of the first list is in the second list and the
    first list is strictly smaller than the second list.
This operator uses the notion of equivalence to determine whether or not two elements are the same.
If the left argument is null, the result is true if the right argument is not empty. Otherwise, if the right argument is null, the result is false.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/
object ProperIncludedInEvaluator {
    @JvmStatic
    fun properlyIncludedIn(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (left == null && right == null) {
            return null
        }

        try {
            if (left == null) {
                return if (right is Interval)
                    ProperIncludesEvaluator.intervalProperlyIncludes(right, null, precision, state)
                else ProperIncludesEvaluator.listProperlyIncludes(right as Iterable<*>, null, state)
            }

            if (right == null) {
                return if (left is Interval)
                    ProperIncludesEvaluator.intervalProperlyIncludes(null, left, precision, state)
                else ProperIncludesEvaluator.listProperlyIncludes(null, left as Iterable<*>, state)
            }

            return ProperIncludesEvaluator.properlyIncludes(right, left, precision, state)
        } catch (e: InvalidOperatorArgument) {
            throw InvalidOperatorArgument(
                "ProperIncludedIn(Interval<T>, Interval<T>) or ProperIncludedIn(List<T>, List<T>)",
                "ProperlyIncludedIn(${left!!.javaClassName}, ${right!!.javaClassName})",
            )
        }
    }
}
