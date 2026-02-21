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

    LowBoundary(input Decimal, precision Integer) Decimal
    LowBoundary(input Date, precision Integer) Date
    LowBoundary(input DateTime, precision Integer) DateTime
    LowBoundary(input Time, precision Integer) Time

    The LowBoundary function returns the least possible value of the input to the specified precision.

    If no precision is specified, the greatest precision of the type of the input value is used (i.e. at least 8 for
        Decimal, 4 for Date, at least 17 for DateTime, and at least 9 for Time).

    If the precision is greater than the maximum possible precision of the implementation, the result is null.

    The function can be used with Decimal, Date, DateTime, and Time values.

    LowBoundary(1.587, 8) // 1.58700000
    LowBoundary(@2014, 6) // @2014-01
    LowBoundary(@2014-01-01T08, 17) // @2014-01-01T08:00:00.000
    LowBoundary(@T10:30, 9) // @T10:30:00.000

    If the input value is null, the result is null.

*/
@Suppress("MagicNumber")
object LowBoundaryEvaluator {
    @JvmStatic
    fun lowBoundary(input: Any?, precision: Any?): Any? {
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

            return input.setScale(precision, RoundingMode.DOWN)
        } else if (input is Date) {
            if (precision == null) {
                precision = 8
            }

            if (precision as Int > 8) {
                return null
            }

            if (precision <= 4) {
                return input.withPrecision(Precision.YEAR)
            } else if (precision <= 6) {
                return input.withPrecision(Precision.MONTH)
            } else if (precision <= 8) {
                return input.withPrecision(Precision.DAY)
            }
        } else if (input is DateTime) {
            if (precision == null) {
                precision = 17
            }

            if (precision as Int > 17) {
                return null
            }

            if (precision <= 4) {
                return input.withPrecision(Precision.YEAR)
            } else if (precision <= 6) {
                return input.withPrecision(Precision.MONTH)
            } else if (precision <= 8) {
                return input.withPrecision(Precision.DAY)
            } else if (precision <= 10) {
                return input.withPrecision(Precision.HOUR)
            } else if (precision <= 12) {
                return input.withPrecision(Precision.MINUTE)
            } else if (precision <= 14) {
                return input.withPrecision(Precision.SECOND)
            } else if (precision <= 17) {
                return input.withPrecision(Precision.MILLISECOND)
            }
        } else if (input is Time) {
            if (precision == null) {
                precision = 9
            }

            if (precision as Int > 9) {
                return null
            }

            if (precision <= 2) {
                return input.withPrecision(Precision.HOUR)
            } else if (precision <= 4) {
                return input.withPrecision(Precision.MINUTE)
            } else if (precision <= 6) {
                return input.withPrecision(Precision.SECOND)
            } else if (precision <= 9) {
                return input.withPrecision(Precision.MILLISECOND)
            }
        }

        throw InvalidOperatorArgument(
            "LowBoundary(Decimal, Integer) or LowBoundary(Date, Integer) or LowBoundary(DateTime, Integer) or LowBoundary(Time, Integer)",
            "LowBoundary(${input.javaClassName}, ${precision!!.javaClassName})",
        )
    }
}
