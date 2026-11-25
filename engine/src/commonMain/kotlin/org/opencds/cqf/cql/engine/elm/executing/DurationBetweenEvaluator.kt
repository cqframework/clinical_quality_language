package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.util.ChronoUnit
import org.opencds.cqf.cql.engine.util.Temporal
import org.opencds.cqf.cql.engine.util.isLastDayOfFeb

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
    fun duration(left: Value?, right: Value?, precision: Precision?): Value? {
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
            val leftRange = possibleRange(left, precision!!)
            val rightRange = possibleRange(right, precision)
            if (leftRange != null || rightRange != null) {
                // The shortest possible duration runs from the latest possible left value to the
                // earliest possible right value, and the longest from the earliest left to the
                // latest right. When they're the same, the duration is known.
                val durationPrecision = if (isWeeks) Precision.WEEK else precision
                val shortest =
                    duration(leftRange?.end ?: left, rightRange?.start ?: right, durationPrecision)
                val longest =
                    duration(leftRange?.start ?: left, rightRange?.end ?: right, durationPrecision)
                return if (shortest == longest) shortest
                else Interval(shortest, true, longest, true, true)
            }

            if (left is DateTime && right is DateTime) {
                // Special case: When calculating duration in years between dates, if both dates
                // fall on the last day of February, the expected result is (see
                // https://cql.hl7.org/15-h-timeintervalcalculations.html#scenario-4):
                //
                // Duration (years) = year (date 2) - year (date 1)
                //
                // Note that
                //
                // ChronoUnit.YEARS.between(LocalDate.of(2012, 2, 29), LocalDate.of(2014, 2, 28))
                //
                // returns 1 (but we expect 2), and
                //
                // LocalDate.of(2012, 2, 29).plusYears(2)
                //
                // returns 2014-02-28 (consistent with the CQL spec).
                if (
                    precision == Precision.YEAR &&
                        left.dateTime.isLastDayOfFeb() &&
                        right.dateTime.isLastDayOfFeb()
                ) {
                    return (right.dateTime.getYear() - left.dateTime.getYear()).toCqlInteger()
                }

                if (precision == Precision.MONTH) {
                    val end = right.dateTime.toLocalDateTime()
                    return wholeMonths(
                            ChronoUnit.MONTHS.between(left.dateTime.toLocalDateTime(), end),
                            { left.dateTime.plus(it, ChronoUnit.MONTHS).toLocalDateTime() },
                            end,
                            ChronoUnit.MILLIS,
                        )
                        .toCqlInteger()
                }

                if (precision.toDateTimeIndex() <= Precision.DAY.toDateTimeIndex()) {
                    return if (isWeeks)
                        (precision
                                .toChronoUnit()
                                .between(
                                    left.dateTime.toLocalDateTime(),
                                    right.dateTime.toLocalDateTime(),
                                )
                                .toInt() / 7)
                            .toCqlInteger()
                    else
                        precision
                            .toChronoUnit()
                            .between(
                                left.dateTime.toLocalDateTime(),
                                right.dateTime.toLocalDateTime(),
                            )
                            .toInt()
                            .toCqlInteger()
                } else {
                    return precision
                        .toChronoUnit()
                        .between(left.dateTime, right.dateTime)
                        .toInt()
                        .toCqlInteger()
                }
            }

            if (left is Date && right is Date) {
                if (
                    precision == Precision.YEAR &&
                        left.date.isLastDayOfFeb() &&
                        right.date.isLastDayOfFeb()
                ) {
                    return (right.date.getYear() - left.date.getYear()).toCqlInteger()
                }

                if (precision == Precision.MONTH) {
                    return wholeMonths(
                            ChronoUnit.MONTHS.between(left.date, right.date),
                            { left.date.plus(it, ChronoUnit.MONTHS) },
                            right.date,
                            ChronoUnit.DAYS,
                        )
                        .toCqlInteger()
                }

                return if (isWeeks)
                    (precision.toChronoUnit().between(left.date, right.date).toInt() / 7)
                        .toCqlInteger()
                else precision.toChronoUnit().between(left.date, right.date).toInt().toCqlInteger()
            }

            if (left is Time && right is Time) {
                return precision
                    .toChronoUnit()
                    .between(left.time, right.time)
                    .toInt()
                    .toCqlInteger()
            }
        }

        throw InvalidOperatorArgument(
            "DurationBetween(Date, Date), DurationBetween(DateTime, DateTime), DurationBetween(Time, Time)",
            "DurationBetween(${left.typeAsString}, ${right.typeAsString})",
        )
    }

    /**
     * Whole calendar months from a start to [end], given [between], the count from
     * `ChronoUnit.MONTHS`, and [plusMonths], which adds months to the start.
     *
     * A month that would end on a day the ending month doesn't have ends on that month's last day
     * (Appendix H, Calculating Duration in Months), which is how [plusMonths] adds months.
     * `ChronoUnit.MONTHS.between` on the JVM doesn't clamp that way: from 2005-12-31T23:59:59.999
     * to 2006-05-01T00:00:00.000 it counts 3 months, though 2005-12-31T23:59:59.999 plus 4 months
     * is 2006-04-30T23:59:59.999. [compareUnit] is the finest unit of the values, used to compare
     * them.
     */
    private fun wholeMonths(
        between: kotlin.Long,
        plusMonths: (kotlin.Long) -> Temporal,
        end: Temporal,
        compareUnit: ChronoUnit,
    ): Int {
        var months = between
        if (months >= 0 && compareUnit.between(plusMonths(months + 1), end) >= 0) {
            months++
        } else if (months <= 0 && compareUnit.between(end, plusMonths(months - 1)) >= 0) {
            months--
        }
        return months.toInt()
    }

    /**
     * The range of values [value] could stand for, or null if it is precise enough to give a single
     * duration at [precision].
     *
     * A Date is uncertain only when it is less precise than the duration. A DateTime or Time is
     * also uncertain when it is less precise than seconds, at any duration precision: its range
     * runs through milliseconds, so `DateTime(2014, 1, 15)` covers all of January 15. Seconds and
     * milliseconds count as a single precision, so a value specified to seconds is not widened
     * unless the duration is in milliseconds.
     */
    private fun possibleRange(value: BaseTemporal, precision: Precision): Interval? {
        val belowSeconds =
            when (value) {
                is DateTime ->
                    value.precision.toDateTimeIndex() < Precision.SECOND.toDateTimeIndex()
                is Time -> value.precision.toTimeIndex() < Precision.SECOND.toTimeIndex()
                else -> false
            }
        return when {
            belowSeconds -> value.getUncertaintyInterval(Precision.MILLISECOND)
            value.isUncertain(precision) -> value.getUncertaintyInterval(precision)
            else -> null
        }
    }
}
