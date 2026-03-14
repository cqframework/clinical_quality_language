package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.RoundingMode
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.util.javaClassName

/*

    HighBoundary(input Decimal, precision Integer) Decimal
    HighBoundary(input Date, precision Integer) Date
    HighBoundary(input DateTime, precision Integer) DateTime
    HighBoundary(input Time, precision Integer) Time

    The HighBoundary function returns the greatest possible value of the input to the specified precision.

    If no precision is specified, the greatest precision of the type of the input value is used (i.e. at least 8 for
        Decimal, 4 for Date, at least 17 for DateTime, and at least 9 for Time).

    If the precision is greater than the maximum possible precision of the implementation, the result is null.

    The function can be used with Decimal, Date, DateTime, and Time values.

    HighBoundary(1.587, 8) // 1.58799999
    HighBoundary(@2014, 6) // @2014-12
    HighBoundary(@2014-01-01T08, 17) // @2014-01-01T08:59:59.999
    HighBoundary(@T10:30, 9) // @T10:30:59.999

    If the input value is null, the result is null.

*/
@Suppress("MagicNumber")
object HighBoundaryEvaluator {
    @JvmStatic
    fun highBoundary(input: Any?, precision: Any?): Any? {
        var precision = precision
        if (input == null) {
            return null
        }

        if (input is BigDecimal) {
            if (precision == null) {
                precision = 8
            }

            if (precision as Int > 8) {
                return null
            }

            val result = BigDecimal(input.toPlainString() + "99999999")
            return result.setScale(precision, RoundingMode.DOWN)
        } else if (input is Date) {
            if (precision == null) {
                precision = 8
            }

            if (precision as Int > 8) {
                return null
            }

            if (precision <= 4) {
                return input.expandPartialMax(Precision.YEAR)
            } else if (precision <= 6) {
                return input.expandPartialMax(Precision.MONTH)
            } else if (precision <= 8) {
                return input.expandPartialMax(Precision.DAY)
            }
        } else if (input is DateTime) {
            if (precision == null) {
                precision = 17
            }

            if (precision as Int > 17) {
                return null
            }

            if (precision <= 4) {
                return input.expandPartialMax(Precision.YEAR)
            } else if (precision <= 6) {
                return input.expandPartialMax(Precision.MONTH)
            } else if (precision <= 8) {
                return input.expandPartialMax(Precision.DAY)
            } else if (precision <= 10) {
                return input.expandPartialMax(Precision.HOUR)
            } else if (precision <= 12) {
                return input.expandPartialMax(Precision.MINUTE)
            } else if (precision <= 14) {
                return input.expandPartialMax(Precision.SECOND)
            } else if (precision <= 17) {
                return input.expandPartialMax(Precision.MILLISECOND)
            }
        } else if (input is Time) {
            if (precision == null) {
                precision = 9
            }

            if (precision as Int > 9) {
                return null
            }

            if (precision <= 2) {
                return input.expandPartialMax(Precision.HOUR)
            } else if (precision <= 4) {
                return input.expandPartialMax(Precision.MINUTE)
            } else if (precision <= 6) {
                return input.expandPartialMax(Precision.SECOND)
            } else if (precision <= 9) {
                return input.expandPartialMax(Precision.MILLISECOND)
            }
        }

        throw InvalidOperatorArgument(
            "HighBoundary(Decimal, Integer) or HighBoundary(Date, Integer) or HighBoundary(DateTime, Integer) or HighBoundary(Time, Integer)",
            "HighBoundary(${input.javaClassName}, ${precision!!.javaClassName})",
        )
    }
}
