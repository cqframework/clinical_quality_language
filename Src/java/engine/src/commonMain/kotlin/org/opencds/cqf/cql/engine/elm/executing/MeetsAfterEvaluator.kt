package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval

/*
meets after _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets after operator returns true if the first interval starts immediately after the second interval ends.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object MeetsAfterEvaluator {
    @JvmStatic
    fun meetsAfter(
        left: CqlType?,
        right: CqlType?,
        precision: kotlin.String?,
        state: State?,
    ): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val isRightStartGreater = GreaterEvaluator.greater(right.start, left.end, state)
            if (isRightStartGreater != null && isRightStartGreater.value) {
                return Boolean.FALSE
            }

            val leftStart = left.start
            val rightEnd = right.end

            var isIn = InEvaluator.`in`(left.end, right, precision, state)
            if (isIn != null && isIn.value) {
                return Boolean.FALSE
            }
            isIn = InEvaluator.`in`(leftStart, right, precision, state)
            if (isIn != null && isIn.value) {
                return Boolean.FALSE
            }
            isIn = InEvaluator.`in`(rightEnd, left, precision, state)
            if (isIn != null && isIn.value) {
                return Boolean.FALSE
            }

            return MeetsEvaluator.meetsOperation(rightEnd, leftStart, precision, state)
        }

        throw InvalidOperatorArgument(
            "MeetsAfter(Interval<T>, Interval<T>)",
            "MeetsAfter(${left.typeAsString}, ${right.typeAsString})",
        )
    }
}
