package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
includes _precision_ (left Interval<T>, right Interval<T>) Boolean

The includes operator for intervals returns true if the first interval completely includes the second.
    More precisely, if the starting point of the first interval is less than or equal to the starting point
    of the second interval, and the ending point of the first interval is greater than or equal to the ending point
    of the second interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
includes(left List<T>, right List<T>) Boolean
includes(left List<T>, right T) Boolean

The includes operator for lists returns true if the first list contains every element of the second list using equality semantics.
For the singleton overload, this operator returns true if the list includes (i.e. contains) the singleton.
If either argument is null, the result is null.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/
object IncludesEvaluator {
    fun includes(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        try {
            return IncludedInEvaluator.includedIn(right, left, precision, state)
        } catch (e: IllegalArgumentException) {
            throw InvalidOperatorArgument(
                "Includes(Interval<T>, Interval<T>), Includes(List<T>, List<T>) or Includes(List<T>, T)",
                "Includes(${left!!.javaClassName}, ${right!!.javaClassName})",
            )
        }
    }

    @JvmStatic
    fun internalEvaluate(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (left == null && right == null) {
            return null
        }

        if (left == null) {
            return if (right is Interval)
                IncludedInEvaluator.intervalIncludedIn(right, null, precision, state)
            else IncludedInEvaluator.listIncludedIn(right as Iterable<*>, null, state)
        }

        if (right == null) {
            return if (left is Interval)
                IncludedInEvaluator.intervalIncludedIn(null, left, precision, state)
            else IncludedInEvaluator.listIncludedIn(null, left as Iterable<*>, state)
        }

        return includes(left, right, precision, state)
    }
}
