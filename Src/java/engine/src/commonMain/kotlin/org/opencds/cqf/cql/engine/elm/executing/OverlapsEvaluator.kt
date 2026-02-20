package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
overlaps _precision_ (left Interval<T>, right Interval<T>) Boolean

The overlaps operator returns true if the first interval overlaps the second.
    More precisely, if the ending point of the first interval is greater than or equal to the
    starting point of the second interval, and the starting point of the first interval is
    less than or equal to the ending point of the second interval.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object OverlapsEvaluator {
    @JvmStatic
    fun overlaps(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val leftStart = left.start
            val leftEnd = left.end
            val rightStart = right.start
            val rightEnd = right.end

            if (leftStart is BaseTemporal && rightStart is BaseTemporal) {
                return AndEvaluator.and(
                    SameOrBeforeEvaluator.sameOrBefore(leftStart, rightEnd, precision, state),
                    SameOrBeforeEvaluator.sameOrBefore(rightStart, leftEnd, precision, state),
                )
            } else {
                return AndEvaluator.and(
                    LessOrEqualEvaluator.lessOrEqual(leftStart, rightEnd, state),
                    LessOrEqualEvaluator.lessOrEqual(rightStart, leftEnd, state),
                )
            }
        }

        throw InvalidOperatorArgument(
            "Overlaps(Interval<T>, Interval<T>)",
            "Overlaps(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
