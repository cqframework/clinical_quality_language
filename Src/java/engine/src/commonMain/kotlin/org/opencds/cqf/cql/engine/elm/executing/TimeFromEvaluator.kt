package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*
time from(argument DateTime) Time

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/
@Suppress("MagicNumber")
object TimeFromEvaluator {
    @JvmStatic
    fun timeFrom(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        if (operand is DateTime) {
            val hour: Int
            if (operand.precision!!.toDateTimeIndex() > 2) {
                hour = operand.dateTime!!.getHour()
            } else {
                return null
            }

            val minute: Int
            if (operand.precision!!.toDateTimeIndex() > 3) {
                minute = operand.dateTime!!.getMinute()
            } else {
                return Time(hour)
            }

            val second: Int
            if (operand.precision!!.toDateTimeIndex() > 4) {
                second = operand.dateTime!!.getSecond()
            } else {
                return Time(hour, minute)
            }

            val millisecond: Int
            if (operand.precision!!.toDateTimeIndex() > 5) {
                millisecond = operand.dateTime!!.get(Precision.MILLISECOND.toChronoField())
            } else {
                return Time(hour, minute, second)
            }

            return Time(hour, minute, second, millisecond)
        }

        throw InvalidOperatorArgument("TimeFrom(DateTime)", "TimeFrom(${operand.javaClassName})")
    }
}
