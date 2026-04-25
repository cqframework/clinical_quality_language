package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidDateTime
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.TemporalHelper

/*
simple type DateTime

The DateTime type represents date and time values with potential uncertainty within CQL.
CQL supports date and time values in the range @0001-01-01T00:00:00.0 to @9999-12-31T23:59:59.999 with a 1 millisecond step size.
*/
object DateTimeEvaluator {
    @JvmStatic
    fun internalEvaluate(
        year: CqlType?,
        month: CqlType?,
        day: CqlType?,
        hour: CqlType?,
        minute: CqlType?,
        second: CqlType?,
        milliSecond: CqlType?,
        timeZoneOffset: CqlType?,
    ): DateTime? {
        if (
            year is Integer? &&
                month is Integer? &&
                day is Integer? &&
                hour is Integer? &&
                minute is Integer? &&
                second is Integer? &&
                milliSecond is Integer? &&
                timeZoneOffset is Decimal
        ) {
            if (year == null) {
                return null
            }

            try {
                return DateTime(
                    timeZoneOffset.value,
                    *TemporalHelper.cleanArray(
                        year.value,
                        month?.value,
                        day?.value,
                        hour?.value,
                        minute?.value,
                        second?.value,
                        milliSecond?.value,
                    ),
                )
            } catch (e: Exception) {
                throw InvalidDateTime("Invalid date time components ${e.message}", e)
            }
        }

        throw InvalidOperatorArgument(
            "DateTime(Integer, Integer, Integer, Integer, Integer, Integer, Integer, Decimal)",
            "DateTime(${year?.typeAsString}, ${month?.typeAsString}, ${day?.typeAsString}, ${hour?.typeAsString}, ${minute?.typeAsString}, ${second?.typeAsString}, ${milliSecond?.typeAsString}, ${timeZoneOffset?.typeAsString})",
        )
    }
}
