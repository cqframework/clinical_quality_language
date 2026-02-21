package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
point from(argument Interval<T>) : T
The point from operator extracts the single point from a unit interval. If the argument is not a unit interval, a run-time error is thrown.

If the argument is null, the result is null.
* */
object PointFromEvaluator {
    @JvmStatic
    fun pointFrom(operand: Any?, state: State?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Interval) {
            val start = operand.start
            val end = operand.end

            val equal = EqualEvaluator.equal(start, end, state)
            if (equal != null && equal) {
                return start
            }

            throw InvalidInterval(
                "Cannot perform PointFrom operation on intervals that are not unit intervals."
            )
        }

        throw InvalidOperatorArgument(
            "PointFrom(Interval<T>)",
            "PointFrom(${operand.javaClassName})",
        )
    }
}
