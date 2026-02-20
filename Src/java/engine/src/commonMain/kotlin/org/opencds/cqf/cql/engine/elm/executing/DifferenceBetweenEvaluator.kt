package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*

difference in _precision_ between(low Date, high Date) Integer
difference in _precision_ between(low DateTime, high DateTime) Integer
difference in _precision_ between(low Time, high Time) Integer

The difference-between operator returns the number of boundaries crossed for the specified precision between the first
    and second arguments. If the first argument is after the second argument, the result is negative. The result of this
    operation is always an integer; any fractional boundaries are dropped.

As with all date/time calculations, difference calculations are performed respecting the timezone offset depending on the precision.

For Date values, precision must be one of: years, months, weeks, or days.
For DateTime values, precision must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.
For Time values, precision must be one of: hours, minutes, seconds, or milliseconds.

For calculations involving weeks, Sunday is considered to be the first day of the week for the purposes of determining the number of boundaries crossed.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

If either argument is null, the result is null.

Additional Complexity: precision elements above the specified precision must also be accounted for.
For example:
days between DateTime(2011, 5, 1) and DateTime(2012, 5, 6) = 365 + 5 = 370 days

NOTE: This is the same operation as DurationBetween, but the precision after the specified precision is truncated
to get the number of boundaries crossed instead of whole calendar periods.
For Example:
difference in days between DateTime(2014, 5, 12, 12, 10) and DateTime(2014, 5, 25, 15, 55)
will truncate the DateTimes to:
DateTime(2014, 5, 12) and DateTime(2014, 5, 25) respectively

*/
object DifferenceBetweenEvaluator {
    @JvmStatic
    fun difference(left: Any?, right: Any?, precision: Precision): Any? {
        var precision = precision
        if (left == null || right == null) {
            return null
        }

        if (left is BaseTemporal && right is BaseTemporal) {
            var isWeeks = false
            if (precision == Precision.WEEK) {
                isWeeks = true
                precision = Precision.DAY
            }
            val isLeftUncertain = left.isUncertain(precision)
            val isRightUncertain = right.isUncertain(precision)
            if (isLeftUncertain && isRightUncertain) {
                return null
            }
            if (isLeftUncertain) {
                val leftUncertainInterval = left.getUncertaintyInterval(precision)!!
                return Interval(
                        difference(
                            leftUncertainInterval.end,
                            right,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                        difference(
                            leftUncertainInterval.start,
                            right,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                    )
                    .setUncertain(true)
            }
            if (isRightUncertain) {
                val rightUncertainInterval = right.getUncertaintyInterval(precision)!!
                return Interval(
                        difference(
                            left,
                            rightUncertainInterval.start,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                        difference(
                            left,
                            rightUncertainInterval.end,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                    )
                    .setUncertain(true)
            }

            if (left is DateTime && right is DateTime) {
                if (precision.toDateTimeIndex() <= Precision.DAY.toDateTimeIndex()) {
                    return if (isWeeks)
                        (precision
                            .toChronoUnit()
                            .between(
                                left
                                    .expandPartialMinFromPrecision(precision)
                                    .dateTime!!
                                    .toLocalDate(),
                                right
                                    .expandPartialMinFromPrecision(precision)
                                    .dateTime!!
                                    .toLocalDate(),
                            )
                            .toInt() / 7)
                    else
                        precision
                            .toChronoUnit()
                            .between(
                                left
                                    .expandPartialMinFromPrecision(precision)
                                    .dateTime!!
                                    .toLocalDate(),
                                right
                                    .expandPartialMinFromPrecision(precision)
                                    .dateTime!!
                                    .toLocalDate(),
                            )
                            .toInt()
                } else {
                    return precision
                        .toChronoUnit()
                        .between(
                            left.expandPartialMinFromPrecision(precision).dateTime!!,
                            right.expandPartialMinFromPrecision(precision).dateTime!!,
                        )
                        .toInt()
                }
            }

            if (left is Date && right is Date) {
                return if (isWeeks)
                    (precision
                        .toChronoUnit()
                        .between(
                            left.expandPartialMinFromPrecision(precision).date!!,
                            right.expandPartialMinFromPrecision(precision).date!!,
                        )
                        .toInt() / 7)
                else
                    precision
                        .toChronoUnit()
                        .between(
                            left.expandPartialMinFromPrecision(precision).date!!,
                            right.expandPartialMinFromPrecision(precision).date!!,
                        )
                        .toInt()
            }

            if (left is Time && right is Time) {
                return precision
                    .toChronoUnit()
                    .between(
                        left.expandPartialMinFromPrecision(precision).time,
                        right.expandPartialMinFromPrecision(precision).time,
                    )
                    .toInt()
            }
        }

        throw InvalidOperatorArgument(
            "DifferenceBetween(Date, Date), DifferenceBetween(DateTime, DateTime), DifferenceBetween(Time, Time)",
            "DifferenceBetween(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
