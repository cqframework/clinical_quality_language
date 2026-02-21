package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.util.ZoneOffset
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    ConvertsToDateTime(argument Date) Boolean
    ConvertsToDateTime(argument String) Boolean

    The ConvertsToDateTime operator returns true if its argument is or can be converted to a DateTime value. See the ToDateTime
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid DateTime value, the result is false.

    As with date and time literals, DateTime values may be specified to any precision. If no timezone offset is supplied,
        the timezone offset of the evaluation request timestamp is assumed.

    If the argument is null, the result is null.

*/
object ConvertsToDateTimeEvaluator {
    @JvmStatic
    fun convertsToDateTime(argument: Any?, offset: ZoneOffset?): Boolean? {
        if (argument == null) {
            return null
        }

        if (argument is DateTime) {
            return true
        }

        if (argument is String) {
            try {
                DateTime(argument, offset!!)
            } catch (dtpe: Exception) {
                return false
            }
            return true
        } else if (argument is Date) {
            try {
                DateTime(
                    TemporalHelper.zoneToOffset(offset!!),
                    argument.date!!.getYear(),
                    argument.date!!.getMonthValue(),
                    argument.date!!.getDayOfMonth(),
                    0,
                    0,
                    0,
                    0,
                )
            } catch (e: Exception) {
                return false
            }
            return true
        }

        throw InvalidOperatorArgument(
            "ConvertsToDateTime(String) or ConvertsToDateTime(Date)",
            "ConvertsToDateTime(${argument.javaClassName})",
        )
    }
}
