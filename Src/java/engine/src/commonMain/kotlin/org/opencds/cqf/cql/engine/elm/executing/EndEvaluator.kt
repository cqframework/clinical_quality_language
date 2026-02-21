package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Interval

/*
end of(argument Interval<T>) T

The End operator returns the ending point of an interval.
If the high boundary of the interval is open, this operator returns the predecessor of the high value of the interval.
  Note that if the high value of the interval is null, the result is null.
If the high boundary of the interval is closed and the high value of the interval is not null,
  this operator returns the high value of the interval.
    Otherwise, the result is the maximum value of the point type of the interval.
If the argument is null, the result is null.
*/
object EndEvaluator {
    @JvmStatic
    fun end(operand: Any?): Any? {
        val argument = operand as Interval?

        if (argument == null) {
            return null
        }

        return argument.end
    }
}
