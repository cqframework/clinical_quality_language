package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    _precision_ from(argument Date) Integer
    _precision_ from(argument DateTime) Integer
    _precision_ from(argument Time) Integer
    timezoneoffset from(argument DateTime) Decimal
    date from(argument DateTime) Date
    time from(argument DateTime) Time

    The component-from operator returns the specified component of the argument.

    For Date values, precision must be one of: year, month, or day.

    For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.

    For Time values, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, extraction of a week component is not supported.

    When extracting the Time from a DateTime value, implementations should normalize to the timezone offset of the evaluation request timestamp.

    If the argument is null, or is not specified to the level of precision being extracted, the result is null.

    The following examples illustrate the behavior of the component-from operator:

    define MonthFrom: month from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // 1
    define TimeZoneOffsetFrom: timezoneoffset from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // -7.0
    define DateFrom: date from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // @2012-01-01
    define TimeFrom: time from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // @T12:30:00.000-07:00
    define MonthFromIsNull: month from DateTime(2012)

*/
object DateTimeComponentFromEvaluator {
    @JvmStatic
    fun dateTimeComponentFrom(operand: Any?, precision: String?): Any? {
        if (operand == null) {
            return null
        }

        if (precision == null) {
            throw InvalidOperatorArgument(
                "Precision must be specified for the _precision_ from operation."
            )
        }

        val p = Precision.fromString(precision)

        if (operand is Date) {
            val date = operand

            if (p.toDateIndex() > date.precision!!.toDateIndex()) {
                return null
            }

            return date.date!!.get(p.toChronoField())
        } else if (operand is DateTime) {
            val dateTime = operand

            if (p.toDateTimeIndex() > dateTime.precision!!.toDateTimeIndex()) {
                return null
            }

            return dateTime.dateTime!!.get(p.toChronoField())
        } else if (operand is Time) {
            val time = operand

            if (p.toTimeIndex() > time.precision!!.toTimeIndex()) {
                return null
            }

            return time.time.get(p.toChronoField())
        }

        throw InvalidOperatorArgument(
            "_precision_ from(Date), _precision_ from(DateTime) or _precision_ from(Time)",
            "${precision.lowercase()} from(${operand.javaClassName})",
        )
    }
}
