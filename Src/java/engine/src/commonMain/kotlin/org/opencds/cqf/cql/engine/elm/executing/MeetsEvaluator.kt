package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*
meets _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets operator returns true if the first interval ends immediately before the second interval starts,
    or if the first interval starts immediately after the second interval ends.
    In other words, if the ending point of the first interval is equal to the predecessor of the starting point of the second,
    or if the starting point of the first interval is equal to the successor of the ending point of the second.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object MeetsEvaluator {
    fun meetsOperation(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        var precision = precision
        if (left == null && right == null) {
            return null
        }

        val maxValue =
            MaxValueEvaluator.maxValue(
                if (left != null) left.javaClassName else right!!.javaClassName
            )
        if (left is BaseTemporal && right is BaseTemporal) {
            val isMax = SameAsEvaluator.sameAs(left, maxValue, precision, state)
            if (isMax != null && isMax) {
                return false
            }

            val tempPrecision = BaseTemporal.getHighestPrecision(left, right)
            if (precision == null && left.isUncertain(Precision.fromString(tempPrecision))) {
                return SameAsEvaluator.sameAs(
                    SuccessorEvaluator.successor(left),
                    right,
                    tempPrecision,
                    state,
                )
            } else if (precision != null && left.isUncertain(Precision.fromString(precision))) {
                return SameAsEvaluator.sameAs(left, right, precision, state)
            }

            if (precision == null) {
                precision = tempPrecision
            }

            // the following blocks adds 1 with the left and check if it is same as right when both
            // params are of type
            // DateTime/Time
            if (left is DateTime && right is DateTime) {
                val dt =
                    DateTime(
                        left.dateTime!!.plus(1, Precision.fromString(precision).toChronoUnit()),
                        (left as BaseTemporal).precision!!,
                    )
                return SameAsEvaluator.sameAs(dt, right, precision, state)
            } else if (left is Time) {
                val t =
                    Time(
                        left.time.plus(1, Precision.fromString(precision).toChronoUnit()),
                        (left as BaseTemporal).precision!!,
                    )
                return SameAsEvaluator.sameAs(t, right, precision, state)
            }
        }

        val isMax = EqualEvaluator.equal(left, maxValue, state)
        if (isMax != null && isMax) {
            return false
        }
        // the following gets the successor of left and check with Equal for params Date
        return EqualEvaluator.equal(SuccessorEvaluator.successor(left), right, state)
    }

    @JvmStatic
    fun meets(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val leftStart = left.start
            val leftEnd = left.end

            var `in` = InEvaluator.`in`(leftStart, right, precision, state)
            if (`in` != null && `in`) {
                return false
            }
            `in` = InEvaluator.`in`(leftEnd, right, precision, state)
            if (`in` != null && `in`) {
                return false
            }

            return OrEvaluator.or(
                MeetsBeforeEvaluator.meetsBefore(left, right, precision, state),
                MeetsAfterEvaluator.meetsAfter(left, right, precision, state),
            )
        }

        throw InvalidOperatorArgument(
            "Meets(Interval<T>, Interval<T>)",
            "Meets(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
