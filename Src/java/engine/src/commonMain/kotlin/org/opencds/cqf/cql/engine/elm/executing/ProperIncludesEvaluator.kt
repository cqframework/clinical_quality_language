package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
properly includes _precision_ (left Interval<T>, right Interval<T>) Boolean

The properly includes operator for intervals returns true if the first interval completely includes the second
    and the first interval is strictly larger than the second. More precisely, if the starting point of the first interval
    is less than or equal to the starting point of the second interval, and the ending point of the first interval is
    greater than or equal to the ending point of the second interval, and they are not the same interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
properly includes(left List<T>, right List<T>) Boolean

The properly includes operator for lists returns true if the first list contains every element of the second list, a
    nd the first list is strictly larger than the second list.
This operator uses the notion of equivalence to determine whether or not two elements are the same.
If the left argument is null, the result is false, else if the right argument is null, the result is true if the left argument is not empty.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/
object ProperIncludesEvaluator {
    @JvmStatic
    fun properlyIncludes(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left == null && right == null) {
            return null
        }

        if (left == null) {
            return if (right is Interval) intervalProperlyIncludes(null, right, precision, state)
            else listProperlyIncludes(null, right as Iterable<*>, state)
        }

        if (right == null) {
            return if (left is Interval) intervalProperlyIncludes(left, null, precision, state)
            else listProperlyIncludes(left as Iterable<*>, null, state)
        }

        if (left is Interval && right is Interval) {
            return intervalProperlyIncludes(left, right, precision, state)
        }
        if (left is Iterable<*> && right is Iterable<*>) {
            return listProperlyIncludes(left, right, state)
        }

        throw InvalidOperatorArgument(
            "ProperlyIncludes(Interval<T>, Interval<T>) or ProperlyIncludes(List<T>, List<T>)",
            "ProperlyIncludes(${left.javaClassName}, ${right.javaClassName})",
        )
    }

    fun intervalProperlyIncludes(
        left: Interval?,
        right: Interval?,
        precision: String?,
        state: State?,
    ): Boolean? {
        if (left == null || right == null) {
            return null
        }

        val leftStart = left.start
        val leftEnd = left.end
        val rightStart = right.start
        val rightEnd = right.end

        if (
            leftStart is BaseTemporal ||
                leftEnd is BaseTemporal ||
                rightStart is BaseTemporal ||
                rightEnd is BaseTemporal
        ) {
            val isSame =
                AndEvaluator.and(
                    SameAsEvaluator.sameAs(leftStart, rightStart, precision, state),
                    SameAsEvaluator.sameAs(leftEnd, rightEnd, precision, state),
                )
            return AndEvaluator.and(
                IncludedInEvaluator.intervalIncludedIn(right, left, precision, state),
                if (isSame == null) null else !isSame,
            )
        }
        return AndEvaluator.and(
            IncludedInEvaluator.intervalIncludedIn(right, left, precision, state),
            NotEqualEvaluator.notEqual(left, right, state),
        )
    }

    fun listProperlyIncludes(left: Iterable<*>?, right: Iterable<*>?, state: State?): Boolean? {
        if (left == null) {
            return false
        }

        val leftCount = left.count()

        if (right == null) {
            return leftCount > 0
        }

        return AndEvaluator.and(
            IncludedInEvaluator.listIncludedIn(right, left, state),
            GreaterEvaluator.greater(leftCount, right.count(), state),
        )
    }
}
