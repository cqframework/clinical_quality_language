package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.util.javaClassName

/*
CalculateAgeInYearsAt(birthDate Date, asOf Date) Integer
CalculateAgeInYearsAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInMonthsAt(birthDate Date, asOf Date) Integer
CalculateAgeInMonthsAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInWeeksAt(birthDate Date, asOf Date) Integer
CalculateAgeInWeeksAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInDaysAt(birthDate Date, asOf Date) Integer
CalculateAgeInDaysAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInHoursAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInMinutesAt(birthDate DateTime, asOf DateTime) Integer
CalculateAgeInSecondsAt(birthDate DateTime, asOf DateTime) Integer

The CalculateAgeAt operators calculate the age of a person born on the given birthdate as of the given date in the precision named in the operator.
If the birthDate is null or the asOf argument is null, the result is null.
The CalculateAgeAt operators are defined in terms of a date/time duration calculation.
  This means that if the given birthDate or asOf are not specified to the level of precision corresponding to the operator being invoked,
    the result will be an uncertainty over the range of possible values, potentially causing some comparisons to return null.
*/
object CalculateAgeAtEvaluator {
    @JvmStatic
    fun calculateAgeAt(birthDate: Any?, asOf: Any?, precision: String?): Any? {
        if (birthDate == null || asOf == null) {
            return null
        }

        if ((birthDate is Date && asOf is Date) || (birthDate is DateTime && asOf is DateTime)) {
            return DurationBetweenEvaluator.duration(
                birthDate,
                asOf,
                Precision.fromString(precision!!),
            )
        }

        throw InvalidOperatorArgument(
            "CalculateAgeInYearsAt(Date, Date), CalculateAgeInYearsAt(DateTime, DateTime), CalculateAgeInMonthsAt(Date, Date), CalculateAgeInMonthsAt(DateTime, DateTime), CalculateAgeInWeeksAt(Date, Date), CalculateAgeInWeeksAt(DateTime, DateTime), CalculateAgeInDaysAt(Date, Date), CalculateAgeInDaysAt(DateTime, DateTime), CalculateAgeInHoursAt(Date, Date), CalculateAgeInHoursAt(DateTime, DateTime), CalculateAgeInMinutesAt(Date, Date), CalculateAgeInMinutesAt(DateTime, DateTime), CalculateAgeInSecondsAt(Date, Date), CalculateAgeInSecondsAt(DateTime, DateTime)",
            "CalculateAgeIn${precision}sAt(${birthDate.javaClassName}, ${asOf.javaClassName})",
        )
    }
}
