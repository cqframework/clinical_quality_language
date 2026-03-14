package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*

ToTime(argument String) Time

The ToTime operator converts the value of its argument to a Time value. The operator expects the string to be formatted
    using ISO-8601 time representation:[1]

hh:mm:ss.fff

In addition, the string must be interpretable as a valid time-of-day value.

For example, the following are valid string representations for time-of-day values:
'14:30:00.0' // 2:30PM

If the input string is not formatted correctly, or does not represent a valid time-of-day value, the result is null.

As with time-of-day literals, time-of-day values may be specified to any precision.

If the argument is null, the result is null.

*/
object ToTimeEvaluator {
    @JvmStatic
    fun toTime(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Time) {
            return operand
        }

        if (operand is String) {
            try {
                return Time(operand)
            } catch (dtpe: Exception) {
                return null
            }
        }

        throw InvalidOperatorArgument("ToTime(String)", "ToTime(${operand.javaClassName})")
    }
}
