package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal

/**
 * Converts time-valued quantities to a common unit for Equal and Equivalent when at least one of
 * them uses a CQL calendar duration keyword (`year`, `weeks`, `days`, …). The UCUM service doesn't
 * know these keywords, so without this, comparisons such as `1 week = 7 days` returned null.
 *
 * Equal (CQL 1.5 and later): "calendar-time duration unit conversions shall be performed according
 * to calendar duration semantics", with `1 year = 12 months`, `1 week = 7 days`, `1 day = 24 hours`
 * and so on down to milliseconds, and "unit conversion between variable length calendar durations
 * (i.e. years and months) and definite-time durations (i.e. days or below) results in null".
 *
 * Equivalent adds `1 year ~ 365 days` and `1 month ~ 30 days`, converting "directly" between them
 * ("using the shortest number of conversion steps (e.g. convert from days to years directly, not
 * from days to months to years)").
 *
 * UCUM definite-time units of a week or less (`'wk'`, `'d'`, `'h'`, `'min'`, `'s'`, `'ms'`) convert
 * the same way as their calendar counterparts. UCUM `'a'` and `'mo'` "are not comparable to
 * calendar duration quantities above days (and weeks)", so they aren't converted here.
 */
internal object CalendarDurationHelper {
    private val millisecondsPer: Map<kotlin.String, BigDecimal> =
        mapOf(
            "week" to BigDecimal("604800000"),
            "weeks" to BigDecimal("604800000"),
            "wk" to BigDecimal("604800000"),
            "day" to BigDecimal("86400000"),
            "days" to BigDecimal("86400000"),
            "d" to BigDecimal("86400000"),
            "hour" to BigDecimal("3600000"),
            "hours" to BigDecimal("3600000"),
            "h" to BigDecimal("3600000"),
            "minute" to BigDecimal("60000"),
            "minutes" to BigDecimal("60000"),
            "min" to BigDecimal("60000"),
            "second" to BigDecimal("1000"),
            "seconds" to BigDecimal("1000"),
            "s" to BigDecimal("1000"),
            "millisecond" to BigDecimal("1"),
            "milliseconds" to BigDecimal("1"),
            "ms" to BigDecimal("1"),
        )

    private val monthsPer: Map<kotlin.String, BigDecimal> =
        mapOf(
            "year" to BigDecimal("12"),
            "years" to BigDecimal("12"),
            "month" to BigDecimal("1"),
            "months" to BigDecimal("1"),
        )

    /** Days in a year or month, for Equivalent only: `1 year ~ 365 days`, `1 month ~ 30 days`. */
    private val daysPer: Map<kotlin.String, BigDecimal> =
        mapOf(
            "year" to BigDecimal("365"),
            "years" to BigDecimal("365"),
            "month" to BigDecimal("30"),
            "months" to BigDecimal("30"),
        )

    private val calendarKeywords: Set<kotlin.String> =
        setOf(
            "year",
            "years",
            "month",
            "months",
            "week",
            "weeks",
            "day",
            "days",
            "hour",
            "hours",
            "minute",
            "minutes",
            "second",
            "seconds",
            "millisecond",
            "milliseconds",
        )

    fun isCalendarKeyword(unit: kotlin.String?): kotlin.Boolean = unit in calendarKeywords

    /**
     * The two values in a common unit, or null if they can't be converted to one. Only applies when
     * at least one unit is a calendar keyword; returns null otherwise.
     *
     * @param equivalence whether years and months may be converted to days (Equivalent), rather
     *   than only to each other (Equal).
     */
    @Suppress("ReturnCount")
    fun inCommonUnit(
        left: Quantity,
        right: Quantity,
        equivalence: kotlin.Boolean,
    ): Pair<BigDecimal, BigDecimal>? {
        val leftUnit = left.unit
        val rightUnit = right.unit
        val leftValue = left.value
        val rightValue = right.value
        if (leftValue == null || rightValue == null) {
            return null
        }
        if (!isCalendarKeyword(leftUnit) && !isCalendarKeyword(rightUnit)) {
            return null
        }

        scaled(leftValue, leftUnit, rightValue, rightUnit, millisecondsPer)?.let {
            return it
        }
        scaled(leftValue, leftUnit, rightValue, rightUnit, monthsPer)?.let {
            return it
        }
        if (equivalence) {
            // A year or month on one side and a definite-time duration on the other: convert the
            // year or month directly to days.
            yearOrMonthAndDefinite(leftValue, leftUnit, rightValue, rightUnit)?.let {
                return it
            }
            yearOrMonthAndDefinite(rightValue, rightUnit, leftValue, leftUnit)?.let {
                return Pair(it.second, it.first)
            }
        }
        return null
    }

    private fun scaled(
        leftValue: BigDecimal,
        leftUnit: kotlin.String?,
        rightValue: BigDecimal,
        rightUnit: kotlin.String?,
        factors: Map<kotlin.String, BigDecimal>,
    ): Pair<BigDecimal, BigDecimal>? {
        val leftFactor = factors[leftUnit]
        val rightFactor = factors[rightUnit]
        return if (leftFactor == null || rightFactor == null) null
        else Pair(leftValue.multiply(leftFactor), rightValue.multiply(rightFactor))
    }

    /**
     * A year or month ([variableValue], converted directly to days) and a definite-time duration
     * ([definiteValue]), both in milliseconds so that no division is needed.
     */
    private fun yearOrMonthAndDefinite(
        variableValue: BigDecimal,
        variableUnit: kotlin.String?,
        definiteValue: BigDecimal,
        definiteUnit: kotlin.String?,
    ): Pair<BigDecimal, BigDecimal>? {
        val days = daysPer[variableUnit]
        val milliseconds = millisecondsPer[definiteUnit]
        return if (days == null || milliseconds == null) null
        else
            Pair(
                variableValue.multiply(days).multiply(millisecondsPerDay),
                definiteValue.multiply(milliseconds),
            )
    }

    private val millisecondsPerDay = BigDecimal("86400000")
}
