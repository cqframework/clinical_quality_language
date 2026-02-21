package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ConvertsToTime(argument String) Time

    The ConvertsToTime operator returns true if its argument is or can be converted to a Time value. See the ToTime operator for
        a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid time-of-day value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToTimeEvaluator {
    @JvmStatic
    fun convertsToTime(argument: Any?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Time) {
            return true
        }

        if (argument is String) {
            try {
                Time(argument)
            } catch (dtpe: Exception) {
                return false
            }
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToTime(String)",
            "ConvertsToTime(${argument.javaClassName})",
        )
    }
}
