package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.util.javaClassName

/*

ToDateTime(argument Date) DateTime
ToDateTime(argument String) DateTime

The ToDateTime operator converts the value of its argument to a DateTime value.

For the string overload, the operator expects the string to be formatted using the ISO-8601 datetime representation:

YYYY-MM-DDThh:mm:ss.fff(((+|-)hh:mm)|Z)[1]

In addition, the string must be interpretable as a valid DateTime value.

For example, the following are valid string representations for DateTime values:

'2014-01-01' // January 1st, 2014
'2014-01-01T14:30:00.0Z' // January 1st, 2014, 2:30PM UTC
'2014-01-01T14:30:00.0-07:00' // January 1st, 2014, 2:30PM Mountain Standard (GMT-7:00)
If the input string is not formatted correctly, or does not represent a valid DateTime value, the result is null.

As with Date and Time literals, DateTime values may be specified to any precision. If no timezone offset is supplied,
    the timezone offset of the evaluation request timestamp is assumed.

For the Date overload, the result will be a DateTime with the time components unspecified, except for the timezone
    offset, which will be set to the timezone offset of the evaluation request timestamp.

If the argument is null, the result is null.

*/
object ToDateTimeEvaluator {
    @JvmStatic
    fun toDateTime(operand: Any?, state: State?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is DateTime) {
            return operand
        }

        if (operand is String) {
            try {
                return DateTime(operand, state!!.evaluationDateTime!!.zoneOffset)
            } catch (dtpe: Exception) {
                return null
            }
        }

        if (operand is Date) {
            return DateTime(
                    TemporalHelper.zoneToOffset(state!!.evaluationZonedDateTime!!.getOffset()),
                    operand.date!!.getYear(),
                    operand.date!!.getMonthValue(),
                    operand.date!!.getDayOfMonth(),
                )
                .withPrecision(operand.precision)
        }

        throw InvalidOperatorArgument(
            "ToDateTime(String) or ToDateTime(Date)",
            "ToDateTime(${operand.javaClassName})",
        )
    }
}
