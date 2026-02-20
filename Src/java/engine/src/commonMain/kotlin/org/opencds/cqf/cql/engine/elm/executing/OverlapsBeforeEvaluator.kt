package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
overlaps before _precision_ (left Interval<T>, right Interval<T>) Boolean

The operator overlaps before returns true if the first interval overlaps the second and starts before it.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object OverlapsBeforeEvaluator {
    @JvmStatic
    fun overlapsBefore(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val leftStart = left.start
            val rightStart = right.start

            if (leftStart is BaseTemporal && rightStart is BaseTemporal) {
                return AndEvaluator.and(
                    BeforeEvaluator.before(leftStart, rightStart, precision, state),
                    OverlapsEvaluator.overlaps(left, right, precision, state),
                )
            } else {
                return AndEvaluator.and(
                    LessEvaluator.less(leftStart, rightStart, state),
                    OverlapsEvaluator.overlaps(left, right, precision, state),
                )
            }
        }

        throw InvalidOperatorArgument(
            "OverlapsBefore(Interval<T>, Interval<T>)",
            "OverlapsBefore(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
