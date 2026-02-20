package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
start of(argument Interval<T>) T

The Start operator returns the starting point of an interval.
If the low boundary of the interval is open, this operator returns the successor of the low value of the interval.
  Note that if the low value of the interval is null, the result is null.
If the low boundary of the interval is closed and the low value of the interval is not null, this operator returns the
  low value of the interval. Otherwise, the result is the minimum value of the point type of the interval.
If the argument is null, the result is null.
*/
object StartEvaluator {
    @JvmStatic
    fun start(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Interval) {
            return operand.start
        }

        throw InvalidOperatorArgument("Start(Interval<T>)", "Start(${operand.javaClassName})")
    }
}
