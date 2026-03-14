package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.util.javaClassName

/*

_duration_ between(low Date, high Date) Integer
_duration_ between(low DateTime, high DateTime) Integer
_duration_ between(low Time, high Time) Integer

The duration-between operator returns the number of whole calendar periods for the specified precision between the first
    and second arguments. If the first argument is after the second argument, the result is negative. The result of this
    operation is always an integer; any fractional periods are dropped.

For Date values, duration must be one of: years, months, weeks, or days.
For DateTime values, duration must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.
For Time values, duration must be one of: hours, minutes, seconds, or milliseconds.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to
    DateTime as defined by the ToDateTime operator.

If either argument is null, the result is null.

Additional Complexity: precision elements above the specified precision must also be accounted.
For example:
days between DateTime(2011, 5, 1) and DateTime(2012, 5, 6) = 365 + 5 = 370 days

*/
object DurationBetweenEvaluator {
    @JvmStatic
    fun duration(left: Any?, right: Any?, precision: Precision?): Any? {
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
            val isLeftUncertain = left.isUncertain(precision!!)
            val isRightUncertain = right.isUncertain(precision)
            if (isLeftUncertain && isRightUncertain) {
                return null
            }
            if (isLeftUncertain) {
                val leftUncertainInterval = left.getUncertaintyInterval(precision)!!
                return Interval(
                        duration(
                            leftUncertainInterval.end,
                            right,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                        duration(
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
                        duration(
                            left,
                            rightUncertainInterval.start,
                            if (isWeeks) Precision.WEEK else precision,
                        ),
                        true,
                        duration(
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
                                left.dateTime!!.toLocalDateTime(),
                                right.dateTime!!.toLocalDateTime(),
                            )
                            .toInt() / 7)
                    else
                        precision
                            .toChronoUnit()
                            .between(
                                left.dateTime!!.toLocalDateTime(),
                                right.dateTime!!.toLocalDateTime(),
                            )
                            .toInt()
                } else {
                    return precision
                        .toChronoUnit()
                        .between(left.dateTime!!, right.dateTime!!)
                        .toInt()
                }
            }

            if (left is Date && right is Date) {
                return if (isWeeks)
                    precision.toChronoUnit().between(left.date!!, right.date!!).toInt() / 7
                else precision.toChronoUnit().between(left.date!!, right.date!!).toInt()
            }

            if (left is Time && right is Time) {
                return precision.toChronoUnit().between(left.time, right.time).toInt()
            }
        }

        throw InvalidOperatorArgument(
            "DurationBetween(Date, Date), DurationBetween(DateTime, DateTime), DurationBetween(Time, Time)",
            "DurationBetween(${left.javaClassName}, ${right.javaClassName})",
        )
    }
}
