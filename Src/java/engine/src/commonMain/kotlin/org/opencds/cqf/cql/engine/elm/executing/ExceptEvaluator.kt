package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.exception.UndefinedResult
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
except(left Interval<T>, right Interval<T>) Interval<T>
The except operator for intervals returns the set difference of two intervals.
  More precisely, this operator returns the portion of the first interval that does not overlap with the second.
  Note that to avoid returning an improper interval, if the second argument is properly contained within the first and
    does not start or end it, this operator returns null.
If either argument is null, the result is null.

except(left List<T>, right List<T>) List<T>
The except operator returns the set difference of two lists.
    More precisely, the operator returns a list with the elements that appear in the first operand
    that do not appear in the second operand.

This operator uses equality semantics to determine whether two elements are the same for the purposes of computing the difference.

The operator is defined with set semantics, meaning that each element will appear in the result at most once,
    and that there is no expectation that the order of the inputs will be preserved in the results.

If either argument is null, the result is null.
*/
object ExceptEvaluator {
    @JvmStatic
    fun except(left: Any?, right: Any?, state: State?): Any? {
        if (left == null) {
            return null
        }

        if (left !is Iterable<*> && right == null) {
            return null
        }

        if (left is Interval) {
            val leftStart = left.start
            val leftEnd = left.end
            val rightStart = (right as Interval).start
            val rightEnd = right.end

            if (leftStart == null || leftEnd == null || rightStart == null || rightEnd == null) {
                return null
            }

            // Return null when:
            // left and right are equal
            // right properly includes left
            // left properly includes right and right doesn't start or end left
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

            val leftEqualRight = EqualEvaluator.equal(left, right, state)
            val rightProperlyIncludesLeft =
                ProperIncludesEvaluator.properlyIncludes(right, left, precision, state)
            val leftProperlyIncludesRight =
                ProperIncludesEvaluator.properlyIncludes(left, right, precision, state)
            val rightStartsLeft = StartsEvaluator.starts(right, left, precision, state)
            val rightEndsLeft = EndsEvaluator.ends(right, left, precision, state)
            val isUndefined =
                AnyTrueEvaluator.anyTrue(
                    listOf(
                        leftEqualRight,
                        rightProperlyIncludesLeft,
                        AndEvaluator.and(
                            leftProperlyIncludesRight,
                            AndEvaluator.and(
                                NotEvaluator.not(rightStartsLeft),
                                NotEvaluator.not(rightEndsLeft),
                            ),
                        ),
                    )
                )

            if (isUndefined != null && isUndefined) {
                return null
            }

            if (GreaterEvaluator.greater(rightStart, leftEnd, state) == true) {
                return left
            } else if (
                AndEvaluator.and(
                    LessEvaluator.less(leftStart, rightStart, state),
                    GreaterEvaluator.greater(leftEnd, rightEnd, state),
                ) == true
            ) {
                return null
            }

            // left interval starts before right interval
            if (
                AndEvaluator.and(
                    LessEvaluator.less(leftStart, rightStart, state),
                    LessOrEqualEvaluator.lessOrEqual(leftEnd, rightEnd, state),
                ) == true
            ) {
                val min =
                    if (
                        LessEvaluator.less(
                            PredecessorEvaluator.predecessor(rightStart),
                            leftEnd,
                            state,
                        ) == true
                    )
                        PredecessorEvaluator.predecessor(rightStart)
                    else leftEnd
                return Interval(leftStart, true, min, true, state)
            } else if (
                AndEvaluator.and(
                    GreaterEvaluator.greater(leftEnd, rightEnd, state),
                    GreaterOrEqualEvaluator.greaterOrEqual(leftStart, rightStart, state),
                ) == true
            ) {
                val max =
                    if (
                        GreaterEvaluator.greater(
                            SuccessorEvaluator.successor(rightEnd),
                            leftStart,
                            state,
                        ) == true
                    )
                        SuccessorEvaluator.successor(rightEnd)
                    else leftStart
                return Interval(max, true, leftEnd, true, state)
            }

            throw UndefinedResult(
                @Suppress("MaxLineLength")
                "The following interval values led to an undefined Except result: leftStart: $leftStart, leftEnd: $leftEnd, rightStart: $rightStart, rightEnd: $rightEnd"
            )
        } else if (left is Iterable<*>) {
            val rightArr = right as Iterable<*>?

            val result: MutableList<Any?> = ArrayList()
            var `in`: Boolean?
            for (leftItem in left) {
                `in` = InEvaluator.`in`(leftItem, rightArr, null, state)
                if (`in` != null && !`in`) {
                    result.add(leftItem)
                }
            }

            return DistinctEvaluator.distinct(result, state)
        }

        throw InvalidOperatorArgument(
            "Except(Interval<T>, Interval<T>) or Except(List<T>, List<T>)",
            "Except(${left.javaClassName}, ${right!!.javaClassName})",
        )
    }
}
