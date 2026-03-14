package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
union(left Interval<T>, right Interval<T>) Interval<T>

The union operator for intervals returns the union of the intervals.
  More precisely, the operator returns the interval that starts at the earliest starting point in either argument,
    and ends at the latest starting point in either argument.
If the arguments do not overlap or meet, this operator returns null.
If either argument is null, the result is null.

*** NOTES FOR LIST ***
union(left List<T>, right List<T>) List<T>

The union operator for lists returns a list with all elements from both arguments.
    Note that duplicates are eliminated during this process; if an element appears in both sources,
    that element will only appear once in the resulting list.
If either argument is null, the result is null.
Note that the union operator can also be invoked with the symbolic operator (|).
*/
object UnionEvaluator {
    @JvmStatic
    fun unionInterval(left: Interval?, right: Interval?, state: State?): Interval? {
        if (left == null || right == null) {
            return null
        }

        val leftStart = left.start
        val leftEnd = left.end
        val rightStart = right.start
        val rightEnd = right.end

        if (leftStart == null || leftEnd == null || rightStart == null || rightEnd == null) {
            return null
        }

        var precision: String? = null
        if (leftStart is BaseTemporal && rightStart is BaseTemporal) {
            precision =
                BaseTemporal.getHighestPrecision(
                    leftStart,
                    leftEnd as BaseTemporal,
                    rightStart,
                    rightEnd as BaseTemporal,
                )
        }

        val overlapsOrMeets =
            OrEvaluator.or(
                OverlapsEvaluator.overlaps(left, right, precision, state),
                MeetsEvaluator.meets(left, right, precision, state),
            )
        if (overlapsOrMeets == null || !overlapsOrMeets) {
            return null
        }

        val min =
            if (LessEvaluator.less(leftStart, rightStart, state) == true) leftStart else rightStart
        val max =
            if (GreaterEvaluator.greater(leftEnd, rightEnd, state) == true) leftEnd else rightEnd

        return Interval(min, true, max, true, state)
    }

    fun unionIterable(left: Iterable<*>?, right: Iterable<*>?, state: State?): Iterable<*>? {
        if (left == null && right == null) {
            return mutableListOf<Any?>()
        }

        if (left == null) {
            return DistinctEvaluator.distinct(right, state)
        }

        if (right == null) {
            return DistinctEvaluator.distinct(left, state)
        }

        // List Logic
        val result: MutableList<Any?> = ArrayList<Any?>()
        for (leftElement in left) {
            result.add(leftElement)
        }

        for (rightElement in right) {
            result.add(rightElement)
        }
        return DistinctEvaluator.distinct(result, state)
    }

    @JvmStatic
    fun union(left: Any?, right: Any?, state: State?): Any? {
        if (left is Interval || right is Interval) {
            return unionInterval(left as Interval?, right as Interval?, state)
        } else if (left is Iterable<*> || right is Iterable<*>) {
            return unionIterable(left as Iterable<*>?, right as Iterable<*>?, state)
        }

        val leftName = if (left != null) left.javaClassName else "<unknown>"
        val rightName = if (right != null) right.javaClassName else "<unknown>"

        throw InvalidOperatorArgument(
            "Union(Interval<T>, Interval<T>) or Union(List<T>, List<T>)",
            "Union(${leftName}, ${rightName})",
        )
    }
}
