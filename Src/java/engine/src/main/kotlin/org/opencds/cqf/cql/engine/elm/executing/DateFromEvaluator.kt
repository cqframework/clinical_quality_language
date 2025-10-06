package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision

/*
date from(argument DateTime) Date

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/
object DateFromEvaluator {
    @JvmStatic
    fun dateFrom(operand: Any?): Date? {
        if (operand == null) {
            return null
        }

        if (operand is DateTime) {
            if (operand.precision!!.toDateTimeIndex() < 1) {
                return Date(operand.dateTime!!.year, 1, 1).withPrecision(Precision.YEAR) as Date
            } else if (operand.precision!!.toDateTimeIndex() < 2) {
                return Date(operand.dateTime!!.year, operand.dateTime!!.monthValue, 1)
                    .withPrecision(Precision.MONTH) as Date
            } else {
                return Date(
                        operand.dateTime!!.year,
                        operand.dateTime!!.monthValue,
                        operand.dateTime!!.dayOfMonth,
                    )
                    .withPrecision(Precision.DAY) as Date
            }
        }

        throw InvalidOperatorArgument(
            "date from(DateTime)",
            String.format("date from(%s)", operand.javaClass.name),
        )
    }
}
