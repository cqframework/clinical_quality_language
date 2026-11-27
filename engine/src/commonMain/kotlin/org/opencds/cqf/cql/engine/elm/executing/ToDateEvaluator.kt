package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.util.zoneOffsetOfHoursMinutes

/*

ToDate(argument String) DateTime

The ToDate operator converts the value of its argument to a Date value.
The operator expects the string to be formatted using the ISO-8601 date representation:
    YYYY-MM-DD

In addition, the string must be interpretable as a valid date value.
For example, the following are valid string representations for date values:
    '2014-01' // January, 2014
    '2014-01-01' // January 1st, 2014
    '2014-01-01T12:30:00' // Still January 1st, 2014, time portion will be ignored

The operator can take datetime formatted strings and will ignore the time portions.
If the input string is not formatted correctly, or does not represent a valid date value, the result is null.
As with date literals, date values may be specified to any precision.
If the argument is null, the result is null.

*/
object ToDateEvaluator {
    @JvmStatic
    fun toDate(operand: Value?): BaseTemporal? {
        if (operand == null) {
            return null
        }

        if (operand is Date) {
            return operand
        }

        if (operand is String) {
            try {
                val value = operand.value
                val timeIndex = value.indexOf('T')
                if (timeIndex < 0) {
                    return Date(value)
                }
                // A datetime-formatted string: the time portion is ignored, but the whole string
                // must still be a valid datetime.
                DateTime(value, zoneOffsetOfHoursMinutes(0, 0))
                return Date(value.substring(0, timeIndex))
            } catch (dtpe: Exception) {
                return null
            }
        }

        if (operand is DateTime) {
            return Date(operand.dateTime.toLocalDate())
                .withPrecision(
                    if (operand.precision.toDateTimeIndex() > 2) Precision.DAY
                    else operand.precision
                )
        }

        throw InvalidOperatorArgument("ToDate(String)", "ToDate(${operand.typeAsString})")
    }
}
