package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/*
timezoneoffset from(argument DateTime) Decimal

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/
object TimezoneOffsetFromEvaluator {
    @JvmStatic
    fun timezoneOffsetFrom(operand: Value?): Decimal? {
        if (operand == null) {
            return null
        }

        if (operand is DateTime) {
            return TemporalHelper.zoneToOffset(operand.dateTime!!.getOffset()).toCqlDecimal()
        }

        throw InvalidOperatorArgument(
            "TimezoneOffsetFrom(DateTime)",
            "TimezoneOffsetFrom(${operand.typeAsString})",
        )
    }
}
