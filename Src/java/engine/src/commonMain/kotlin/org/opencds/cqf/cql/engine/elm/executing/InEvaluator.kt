package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
*** NOTES FOR INTERVAL ***
in(point T, argument Interval<T>) Boolean

The in operator for intervals returns true if the given point is greater than or equal to the
    starting point of the interval, and less than or equal to the ending point of the interval.
    For open interval boundaries, exclusive comparison operators are used.
    For closed interval boundaries, if the interval boundary is null, the result of the boundary comparison is considered true.
If precision is specified and the point type is a date/time type, comparisons used in the
    operation are performed at the specified precision.
If either argument is null, the result is null.

*/
/*
*** NOTES FOR LIST ***
in(element T, argument List<T>) Boolean

The in operator for lists returns true if the given element is in the given list using equality semantics.

If either argument is null, the result is null.

*/
object InEvaluator {
    fun `in`(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (right == null) {
            return false
        }

        if (right is Iterable<*>) {
            return listIn(left, right, state)
        } else if (right is Interval) {
            return intervalIn(left, right, precision, state)
        }

        throw InvalidOperatorArgument(
            "In(T, Interval<T>) or In(T, List<T>)",
            "In(${left!!.javaClassName}, ${right.javaClassName})",
        )
    }

    private fun intervalIn(
        left: Any?,
        right: Interval,
        precision: String?,
        state: State?,
    ): Boolean? {
        val rightStart = right.start
        val rightEnd = right.end

        if (left is BaseTemporal) {
            if (
                AnyTrueEvaluator.anyTrue(
                    listOf<Boolean?>(
                        SameAsEvaluator.sameAs(left, right.start, precision, state),
                        SameAsEvaluator.sameAs(left, right.end, precision, state),
                    )
                ) == true
            ) {
                return true
            } else if (
                AnyTrueEvaluator.anyTrue(
                    listOf<Boolean?>(
                        BeforeEvaluator.before(left, right.start, precision, state),
                        AfterEvaluator.after(left, right.end, precision, state),
                    )
                ) == true
            ) {
                return false
            }

            val pointSameOrAfterStart: Boolean?
            if (rightStart == null && right.lowClosed) {
                pointSameOrAfterStart = true
            } else {
                pointSameOrAfterStart =
                    SameOrAfterEvaluator.sameOrAfter(left, rightStart, precision, state)
            }

            val pointSameOrBeforeEnd: Boolean?
            if (rightEnd == null && right.highClosed) {
                pointSameOrBeforeEnd = true
            } else {
                pointSameOrBeforeEnd =
                    SameOrBeforeEvaluator.sameOrBefore(left, rightEnd, precision, state)
            }

            return AndEvaluator.and(pointSameOrAfterStart, pointSameOrBeforeEnd)
        } else if (
            AnyTrueEvaluator.anyTrue(
                listOf<Boolean?>(
                    EqualEvaluator.equal(left, right.start, state),
                    EqualEvaluator.equal(left, right.end, state),
                )
            ) == true
        ) {
            return true
        } else if (
            AnyTrueEvaluator.anyTrue(
                listOf<Boolean?>(
                    LessEvaluator.less(left, right.start, state),
                    GreaterEvaluator.greater(left, right.end, state),
                )
            ) == true
        ) {
            return false
        }

        val greaterOrEqual: Boolean?
        if (rightStart == null && right.lowClosed) {
            greaterOrEqual = true
        } else {
            greaterOrEqual = GreaterOrEqualEvaluator.greaterOrEqual(left, rightStart, state)
        }

        val lessOrEqual: Boolean?
        if (rightEnd == null && right.highClosed) {
            lessOrEqual = true
        } else {
            lessOrEqual = LessOrEqualEvaluator.lessOrEqual(left, rightEnd, state)
        }

        return AndEvaluator.and(greaterOrEqual, lessOrEqual)
    }

    private fun listIn(left: Any?, right: Iterable<*>, state: State?): Boolean {
        var isEqual: Boolean?
        for (element in right) {
            // Nulls are considered equivalent in lists
            // Other elements use equality semantics
            if (element == null && left == null) {
                return true
            }

            if (state!!.engineOptions.contains(CqlEngine.Options.EnableHedisCompatibilityMode)) {
                isEqual = EquivalentEvaluator.equivalent(left, element, state)
            } else {
                isEqual = EqualEvaluator.equal(left, element, state)
            }

            if (true == isEqual) {
                return true
            }
        }

        return false
    }

    @JvmStatic
    fun internalEvaluate(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        // null right operand case
        //        if (getOperand().get(1) instanceof AsEvaluator) {
        //            if (((AsEvaluator) getOperand().get(1)).getAsTypeSpecifier() instanceof
        // IntervalTypeSpecifier) {
        //                return intervalIn(left, (Interval) right, precision);
        //            }
        //            else {
        //                return listIn(left, (Iterable) right);
        //            }
        //        }

        return `in`(left, right, precision, state)
    }
}
