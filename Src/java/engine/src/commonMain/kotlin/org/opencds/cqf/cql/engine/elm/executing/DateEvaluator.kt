package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.Precision

object DateEvaluator {
    @JvmStatic
    fun internalEvaluate(year: Int?, month: Int?, day: Int?): Any? {
        var month = month
        var day = day
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
}
