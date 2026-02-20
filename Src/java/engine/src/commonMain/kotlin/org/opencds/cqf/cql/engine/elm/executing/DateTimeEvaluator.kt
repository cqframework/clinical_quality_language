package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidDateTime
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.TemporalHelper

/*
simple type DateTime

The DateTime type represents date and time values with potential uncertainty within CQL.
CQL supports date and time values in the range @0001-01-01T00:00:00.0 to @9999-12-31T23:59:59.999 with a 1 millisecond step size.
*/
object DateTimeEvaluator {
    @JvmStatic
    fun internalEvaluate(
        year: Int?,
        month: Int?,
        day: Int?,
        hour: Int?,
        minute: Int?,
        second: Int?,
        milliSecond: Int?,
        timeZoneOffset: BigDecimal?,
    ): Any? {
        if (year == null) {
            return null
        }

        try {
            return DateTime(
                timeZoneOffset!!,
                *TemporalHelper.cleanArray(year, month, day, hour, minute, second, milliSecond),
            )
        } catch (e: Exception) {
            throw InvalidDateTime("Invalid date time components ${e.message}", e)
        }
    }
}
