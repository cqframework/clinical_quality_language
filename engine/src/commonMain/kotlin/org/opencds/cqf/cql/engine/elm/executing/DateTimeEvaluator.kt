package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidDateTime
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Value

/*
simple type DateTime

The DateTime type represents date and time values with potential uncertainty within CQL.
CQL supports date and time values in the range @0001-01-01T00:00:00.0 to @9999-12-31T23:59:59.999 with a 1 millisecond step size.
*/
object DateTimeEvaluator {
    @JvmStatic
    fun internalEvaluate(
        year: Value?,
        month: Value?,
        day: Value?,
        hour: Value?,
        minute: Value?,
        second: Value?,
        milliSecond: Value?,
        timeZoneOffset: Value?,
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
