package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
included in _precision_ (left Interval<T>, right Interval<T>) Boolean

The included in operator for intervals returns true if the first interval is completely included in the second.
    More precisely, if the starting point of the first interval is greater than or equal to the starting point
    of the second interval, and the ending point of the first interval is less than or equal to the ending point
    of the second interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
Note that during is a synonym for included in and may be used to invoke the same operation whenever included in may appear.

*** NOTES FOR LIST ***
included in(left List<T>, right list<T>) Boolean
included in(left T, right list<T>) Boolean

The included in operator for lists returns true if every element of the first list is in the second list using equality semantics.
For the singleton overload, this operator returns true if the singleton is included in (i.e. in) the list.
If either argument is null, the result is null.
Note that the order of elements does not matter for the purposes of determining inclusion.
*/
object IncludedInEvaluator {
    fun includedIn(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left is Interval && right is Interval) {
            return intervalIncludedIn(left, right, precision, state)
        }
        if (left is Iterable<*> && right is Iterable<*>) {
            return listIncludedIn(left, right, state)
        }

        throw InvalidOperatorArgument(
            "IncludedIn(Interval<T>, Interval<T>), IncludedIn(List<T>, List<T>) or IncludedIn(T, List<T>)",
            "IncludedIn(${left!!.javaClassName}, ${right!!.javaClassName})",
        )
    }

    fun intervalIncludedIn(
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

        val boundaryCheck =
            AndEvaluator.and(
                InEvaluator.`in`(leftStart, right, precision, state),
                InEvaluator.`in`(leftEnd, right, precision, state),
            )

        if (boundaryCheck != null && boundaryCheck) {
            return true
        }

        if (
            leftStart is BaseTemporal ||
                leftEnd is BaseTemporal ||
                rightStart is BaseTemporal ||
                rightEnd is BaseTemporal
        ) {
            if (
                AnyTrueEvaluator.anyTrue(
                    listOf<Boolean?>(
                        BeforeEvaluator.before(leftStart, rightStart, precision, state),
                        AfterEvaluator.after(leftEnd, rightEnd, precision, state),
                    )
                ) == true
            ) {
                return false
            }
            return AndEvaluator.and(
                SameOrAfterEvaluator.sameOrAfter(leftStart, rightStart, precision, state),
                SameOrBeforeEvaluator.sameOrBefore(leftEnd, rightEnd, precision, state),
            )
        }

        if (
            AnyTrueEvaluator.anyTrue(
                listOf<Boolean?>(
                    LessEvaluator.less(leftStart, rightStart, state),
                    GreaterEvaluator.greater(leftEnd, rightEnd, state),
                )
            ) == true
        ) {
            return false
        }
        return AndEvaluator.and(
            GreaterOrEqualEvaluator.greaterOrEqual(leftStart, rightStart, state),
            LessOrEqualEvaluator.lessOrEqual(leftEnd, rightEnd, state),
        )
    }

    fun listIncludedIn(left: Iterable<*>?, right: Iterable<*>?, state: State?): Boolean? {
        if (right == null) {
            return false
        }

        if (left == null) {
            // For singleton values, include in is equivalent to in.
            return InEvaluator.`in`(null, right, null, state)
        }

        for (element in left) {
            val `in`: Any? = InEvaluator.`in`(element, right, null, state)

            if (`in` == null) continue

            if (!(`in` as Boolean)) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun internalEvaluate(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (left == null && right == null) {
            return null
        }

        if (left == null) {
            return if (right is Interval) intervalIncludedIn(null, right, precision, state)
            else listIncludedIn(null, right as Iterable<*>, state)
        }

        if (right == null) {
            return if (left is Interval) intervalIncludedIn(left, null, precision, state)
            else listIncludedIn(left as Iterable<*>, null, state)
        }

        return includedIn(left, right, precision, state)
    }
}
