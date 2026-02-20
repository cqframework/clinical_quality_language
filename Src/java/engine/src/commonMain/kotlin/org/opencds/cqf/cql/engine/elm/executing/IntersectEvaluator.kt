package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
intersect(left Interval<T>, right Interval<T>) Interval<T>

The intersect operator for intervals returns the intersection of two intervals.
  More precisely, the operator returns the interval that defines the overlapping portion of both arguments.
If the arguments do not overlap, this operator returns null.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
intersect(left List<T>, right List<T>) List<T>

The intersect operator for lists returns the intersection of two lists.
  More precisely, the operator returns a list containing only the elements that appear in both lists.
This operator uses equality semantics to determine whether or not two elements are the same.
The operator is defined with set semantics, meaning that each element will appear in the result at most once,
    and that there is no expectation that the order of the inputs will be preserved in the results.
If either argument is null, the result is null.
*/
object IntersectEvaluator {
    @JvmStatic
    fun intersect(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval) {
            val leftInterval = left
            val rightInterval = right as Interval

            val leftStart = leftInterval.start
            val leftEnd = leftInterval.end
            val rightStart = rightInterval.start
            val rightEnd = rightInterval.end

            var precision: String? = null
            if (leftStart is BaseTemporal && rightStart is BaseTemporal) {
                precision =
                    BaseTemporal.getHighestPrecision(
                        leftStart,
                        leftEnd as BaseTemporal?,
                        rightStart,
                        rightEnd as BaseTemporal?,
                    )
            }

            val overlaps = OverlapsEvaluator.overlaps(leftInterval, rightInterval, precision, state)
            if (overlaps != null && !overlaps) {
                return null
            }

            val leftStartGtRightStart = GreaterEvaluator.greater(leftStart, rightStart, state)
            val leftEndLtRightEnd = LessEvaluator.less(leftEnd, rightEnd, state)

            val max: Any?
            if (leftStart == null || rightStart == null) {
                // If either of the start points is null, the start point of the intersection is
                // null because the
                // boundary is unknown.
                max = null
            } else if (leftStartGtRightStart == null && precision != null) {
                // It is possible for leftStartGtRightStart to be null without either leftStart or
                // rightStart being null
                // if one has a value for the precision and the other does not, see:
                // https://cql.hl7.org/09-b-cqlreference.html#greater
                max =
                    if ((leftStart as BaseTemporal).precision.toString() == precision) leftStart
                    else rightStart
            } else {
                max =
                    if (leftStartGtRightStart == null) null
                    else if (leftStartGtRightStart) leftStart else rightStart
            }

            val min: Any?
            if (leftEnd == null || rightEnd == null) {
                min = null
            } else if (leftEndLtRightEnd == null && precision != null) {
                min =
                    if ((leftEnd as BaseTemporal).precision.toString() == precision) leftEnd
                    else rightEnd
            } else {
                min =
                    if (leftEndLtRightEnd == null) null
                    else if (leftEndLtRightEnd) leftEnd else rightEnd
            }

            return Interval(max, max != null, min, min != null, state)
        } else if (left is Iterable<*>) {
            val leftArr = left
            val rightArr = right as Iterable<*>

            val result: MutableList<Any?> = ArrayList<Any?>()
            var `in`: Boolean?
            for (leftItem in leftArr) {
                `in` = InEvaluator.`in`(leftItem, rightArr, null, state)
                if (`in` != null && `in`) {
                    result.add(leftItem)
                }
            }

            return DistinctEvaluator.distinct(result, state)
        }

        throw InvalidOperatorArgument(
            "Intersect(Interval<T>, Interval<T>) or Intersect(List<T>, List<T>)",
            "Intersect(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
