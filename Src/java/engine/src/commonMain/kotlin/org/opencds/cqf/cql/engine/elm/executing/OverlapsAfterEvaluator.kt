package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
overlaps after _precision_ (left Interval<T>, right Interval<T>) Boolean

The overlaps after operator returns true if the first interval overlaps the second and ends after it.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object OverlapsAfterEvaluator {
    @JvmStatic
    fun overlapsAfter(left: Any?, right: Any?, precision: String?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        if (left is Interval && right is Interval) {
            val leftEnd = left.end
            val rightEnd = right.end

            if (leftEnd is BaseTemporal && rightEnd is BaseTemporal) {
                return AndEvaluator.and(
                    AfterEvaluator.after(leftEnd, rightEnd, precision, state),
                    OverlapsEvaluator.overlaps(left, right, precision, state),
                )
            } else {
                return AndEvaluator.and(
                    GreaterEvaluator.greater(leftEnd, rightEnd, state),
                    OverlapsEvaluator.overlaps(left, right, precision, state),
                )
            }
        }

        throw InvalidOperatorArgument(
            "OverlapsAfter(Interval<T>, Interval<T>)",
            "Overlaps(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
