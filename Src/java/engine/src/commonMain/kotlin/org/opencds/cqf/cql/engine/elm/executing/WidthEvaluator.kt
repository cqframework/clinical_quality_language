package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.util.javaClassName

/*
width of(argument Interval<T>) T

The width operator returns the width of an interval.
The result of this operator is equivalent to invoking: (start of argument – end of argument) + point-size.
Note that because CQL defines duration and difference operations for date/time and time valued intervals,
  width is not defined for intervals of these types.
If the argument is null, the result is null.
*/
object WidthEvaluator {
    @JvmStatic
    fun width(operand: Any?, state: State?): Any? {
        if (operand == null) {
            return null
        }
        if (operand is Interval) {
            return Interval.getSize(operand.start, operand.end, state)
        }

        throw InvalidOperatorArgument("Width(Interval<T>)", "Width(${operand.javaClassName})")
    }
}
