package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*

*** NOTES FOR ARITHMETIC OPERATOR ***
-(left Integer, right Integer) Integer
-(left Decimal, right Decimal) Decimal
-(left Quantity, right Quantity) Quantity

The subtract (-) operator performs numeric subtraction of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
When subtracting quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
  For example, units of 'cm' and 'm' can be subtracted, but units of 'cm2' and  'cm' cannot.
    The unit of the result will be the most granular unit of either input.
If either argument is null, the result is null.

*** NOTES FOR DATETIME ***
-(left Date, right Quantity) Date
-(left DateTime, right Quantity) DateTime
-(left Time, right Quantity) Time

The subtract (-) operator returns the value of the given date/time, decremented by the time-valued quantity, respecting
    variable length periods for calendar years and months.

For Date values, the quantity unit must be one of: years, months, weeks, or days.

For DateTime values, the quantity unit must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.

For Time values, the quantity unit must be one of: hours, minutes, seconds, or milliseconds.

Note that the quantity units may be specified in singular, plural or UCUM form.

The operation is performed by converting the time-based quantity to the most precise value specified in the date/time
    (truncating any resulting decimal portion) and then subtracting it from the date/time value.
    For example, the following subtraction:
        DateTime(2014) - 24 months
    This example results in the value DateTime(2012) even though the date/time value is not specified to the level of precision of the time-valued quantity.

Note also that this means that if decimals appear in the time-valued quantities, the fractional component will be ignored.
    For example, the following subtraction:
        DateTime(2014) - 18 months
    This example results in the value DateTime(2013)

If either argument is null, the result is null.

NOTE: see note in AddEvaluator

*/
@Suppress("LongMethod", "CyclomaticComplexMethod", "ReturnCount")
object SubtractEvaluator {
    @JvmStatic
    fun subtract(left: Any?, right: Any?, state: State?): Any? {
        if (left == null || right == null) {
            return null
        }

        // -(Integer, Integer)
        if (left is Int) {
            return left - right as Int
        } else if (left is Long) {
            return left - right as Long
        } else if (left is BigDecimal) {
            return left.subtract(right as BigDecimal)
        } else if (left is Quantity) {
            right as Quantity
            return computeWithConvertedUnits(
                left,
                right,
                { commonUnit, leftValue, rightValue ->
                    Quantity().withUnit(commonUnit).withValue(leftValue.subtract(rightValue))
                },
                state!!,
            )
        } else if (left is BaseTemporal && right is Quantity) {
            var valueToSubtractPrecision = Precision.fromString(right.unit!!)
            val precision = Precision.fromString(BaseTemporal.getLowestPrecision(left))
            var valueToSubtract = right.value!!.toInt()

            if (left is DateTime || left is Date) {
                if (valueToSubtractPrecision == Precision.WEEK) {
                    valueToSubtract = TemporalHelper.weeksToDays(valueToSubtract)
                    valueToSubtractPrecision = Precision.DAY
                }
            }

            var convertedValueToSubtract = valueToSubtract.toLong()
            if (precision.toDateTimeIndex() < valueToSubtractPrecision.toDateTimeIndex()) {
                convertedValueToSubtract =
                    TemporalHelper.truncateValueToTargetPrecision(
                        valueToSubtract.toLong(),
                        valueToSubtractPrecision,
                        precision,
                    )
                valueToSubtractPrecision = precision
            }

            if (left is DateTime) {
                return DateTime(
                    left.dateTime!!.minus(
                        convertedValueToSubtract,
                        valueToSubtractPrecision.toChronoUnit(),
                    ),
                    precision,
                )
            } else if (left is Date) {
                return Date(
                        left.date!!.minus(
                            convertedValueToSubtract,
                            valueToSubtractPrecision.toChronoUnit(),
                        )
                    )
                    .withPrecision(precision)
            } else {
                return Time(
                    (left as Time)
                        .time
                        .minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()),
                    precision,
                )
            }
        } else if (left is Interval && right is Interval) {
            val leftInterval = left
            val rightInterval = right
            return Interval(
                subtract(leftInterval.start, rightInterval.start, state),
                true,
                subtract(leftInterval.end, rightInterval.end, state),
                true,
            )
        }

        throw InvalidOperatorArgument(
            "Subtract(Integer, Integer), Subtract(Long, Long) Subtract(Decimal, Decimal), Subtract(Quantity, Quantity), Subtract(Date, Quantity), Subtract(DateTime, Quantity), Subtract(Time, Quantity)",
            "Subtract(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
