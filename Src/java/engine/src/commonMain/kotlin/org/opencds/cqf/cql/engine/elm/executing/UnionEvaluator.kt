package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

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
        if (overlapsOrMeets == null || !overlapsOrMeets.value) {
            return null
        }

        val min =
            if (LessEvaluator.less(leftStart, rightStart, state)?.value == true) leftStart
            else rightStart
        val max =
            if (GreaterEvaluator.greater(leftEnd, rightEnd, state)?.value == true) leftEnd
            else rightEnd

        return Interval(min, true, max, true, state)
    }

    fun unionIterable(left: List?, right: List?, state: State?): List? {
        if (left == null && right == null) {
            return mutableListOf<Value?>().toCqlList()
        }

        if (left == null) {
            return DistinctEvaluator.distinct(right, state)
        }

        if (right == null) {
            return DistinctEvaluator.distinct(left, state)
        }

        // List Logic
        val result = mutableListOf<Value?>()
        for (leftElement in left) {
            result.add(leftElement)
        }

        for (rightElement in right) {
            result.add(rightElement)
        }
        return DistinctEvaluator.distinct(result.toCqlList(), state)
    }

    @JvmStatic
    fun union(left: Value?, right: Value?, state: State?): Value? {
        if (left is Interval? && right is Interval?) {
            return unionInterval(left, right, state)
        } else if (left is List? && right is List?) {
            return unionIterable(left, right, state)
        }

        throw InvalidOperatorArgument(
            "Union(Interval<T>, Interval<T>) or Union(List<T>, List<T>)",
            "Union(${left?.typeAsString}, ${right?.typeAsString})",
        )
    }
}
