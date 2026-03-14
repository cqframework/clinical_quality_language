package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
ends _precision_ (left Interval<T>, right Interval<T>) Boolean

The ends operator returns true if the first interval ends the second.
    More precisely, if the starting point of the first interval is greater than or equal to the starting point of the second,
    and the ending point of the first interval is equal to the ending point of the second.
This operator uses the semantics described in the start and end operators to determine interval boundaries.
If precision is specified and the point type is a date/time type, comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
*/
object EndsEvaluator {
    @JvmStatic
    fun ends(left: Any?, right: Any?, precision: String?, state: State?): Boolean? {
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
                    SameOrAfterEvaluator.sameOrAfter(leftStart, rightStart, precision, state),
                    SameAsEvaluator.sameAs(leftEnd, rightEnd, precision, state),
                )
            } else {
                return AndEvaluator.and(
                    GreaterOrEqualEvaluator.greaterOrEqual(leftStart, rightStart, state),
                    EqualEvaluator.equal(leftEnd, rightEnd, state),
                )
            }
        }

        throw InvalidOperatorArgument(
            "Ends(Interval<T>, Interval<T>)",
            "Ends(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
