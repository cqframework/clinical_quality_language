package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
meets before _precision_ (left Interval<T>, right Interval<T>) Boolean

The meets before operator returns true if the first interval ends immediately before the second interval starts.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object MeetsBeforeEvaluator {
    @JvmStatic
    fun meetsBefore(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val isLeftStartGreater = GreaterEvaluator.greater(left.start, right.end, state)
            if (isLeftStartGreater != null && isLeftStartGreater) {
                return false
            }

            val leftEnd = left.end
            val rightStart = right.start

            var isIn = InEvaluator.`in`(leftEnd, right, precision, state)
            if (isIn != null && isIn) {
                return false
            }
            isIn = InEvaluator.`in`(left.start, right, precision, state)
            if (isIn != null && isIn) {
                return false
            }
            isIn = InEvaluator.`in`(leftEnd, right, precision, state)
            if (isIn != null && isIn) {
                return false
            }

            return MeetsEvaluator.meetsOperation(leftEnd, rightStart, precision, state)
        }

        throw InvalidOperatorArgument(
            "MeetsBefore(Interval<T>, Interval<T>)",
            "MeetsBefore(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
