package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    Precision(argument Decimal) Integer
    Precision(argument Date) Integer
    Precision(argument DateTime) Integer
    Precision(argument Time) Integer

    The Precision function returns the number of digits of precision in the input value.

    The function can be used with Decimal, Date, DateTime, and Time values.

    For Decimal values, the function returns the number of digits of precision after the decimal place in the input value.
    Precision(1.58700) // 5

    For Date and DateTime values, the function returns the number of digits of precision in the input value.
    Precision(@2014) // 4
    Precision(@2014-01-05T10:30:00.000) // 17
    Precision(@T10:30) // 4
    Precision(@T10:30:00.000) // 9
    If the argument is null, the result is null.

*/
object PrecisionEvaluator {
    @JvmStatic
    fun precision(argument: Any?): Int? {
        if (argument == null) {
            return null
        }

        if (argument is BigDecimal) {
            val string = argument.toPlainString()
            val index = string.indexOf(".")
            return if (index < 0) 0 else string.length - index - 1
        } else if (argument is Date) {
            return argument.toString().replace("-".toRegex(), "").length
        } else if (argument is DateTime) {
            return argument
                .toString()
                .replace("(:?[+-][0-9]{2}:[0-9]{2}$|[T.:-]|)".toRegex(), "")
                .length
        } else if (argument is Time) {
            return argument.toString().replace("[T.:]".toRegex(), "").length
        }

        throw InvalidOperatorArgument(
            "Precision(Decimal), Precision(Date), Precision(DateTime) or Precision(Time)",
            "Precision(${argument.javaClassName})",
        )
    }
}
