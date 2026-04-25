package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Precision

object DateEvaluator {
    @JvmStatic
    fun internalEvaluate(year: CqlType?, month: CqlType?, day: CqlType?): BaseTemporal? {
        if (year is Integer? && month is Integer? && day is Integer?) {
            val year = year?.value
            var month = month?.value
            var day = day?.value
            if (year == null) {
                return null
            }
            var precision = Precision.YEAR

            if (month == null) {
                month = 1
            } else {
                precision = Precision.MONTH
            }

            if (day == null) {
                day = 1
            } else {
                precision = Precision.DAY
            }

            return Date(year, month, day).withPrecision(precision)
        }

        throw InvalidOperatorArgument(
            "Date(Integer, Integer, Integer)",
            "Date(${year?.typeAsString}, ${month?.typeAsString}, ${day?.typeAsString})",
        )
    }
}
