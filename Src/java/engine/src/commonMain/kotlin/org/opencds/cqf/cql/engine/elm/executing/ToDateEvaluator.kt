package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.util.javaClassName

/*

ToDate(argument String) DateTime

The ToDate operator converts the value of its argument to a Date value.
The operator expects the string to be formatted using the ISO-8601 date representation:
    YYYY-MM-DD

In addition, the string must be interpretable as a valid date value.
For example, the following are valid string representations for date values:
    '2014-01' // January, 2014
    '2014-01-01' // January 1st, 2014

If the input string is not formatted correctly, or does not represent a valid date value, the result is null.
As with date literals, date values may be specified to any precision.
If the argument is null, the result is null.

*/
object ToDateEvaluator {
    @JvmStatic
    fun toDate(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is Date) {
            return operand
        }

        if (operand is String) {
            try {
                return Date(operand)
            } catch (dtpe: Exception) {
                return null
            }
        }

        if (operand is DateTime) {
            return Date(operand.dateTime!!.toLocalDate())
                .withPrecision(
                    if (operand.precision!!.toDateTimeIndex() > 2) Precision.DAY
                    else operand.precision
                )
        }

        throw InvalidOperatorArgument("ToDate(String)", "ToDate(${operand.javaClassName})")
    }
}
