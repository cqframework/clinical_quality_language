package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Time

/*

    ConvertsToTime(argument String) Time

    The ConvertsToTime operator returns true if its argument is or can be converted to a Time value. See the ToTime operator for
        a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid time-of-day value, the result is false.

    If the argument is null, the result is null.

*/
object ConvertsToTimeEvaluator {
    @JvmStatic
    fun convertsToTime(argument: CqlType?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is Time) {
            return Boolean.TRUE
        }

        if (argument is String) {
            try {
                Time(argument.value)
            } catch (dtpe: Exception) {
                return Boolean.FALSE
            }
            return Boolean.TRUE
        }

        throw InvalidOperatorArgument(
            "ConvertsToTime(String)",
            "ConvertsToTime(${argument.typeAsString})",
        )
    }
}
