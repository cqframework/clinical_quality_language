package org.opencds.cqf.cql.engine.runtime

import kotlin.jvm.JvmStatic
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.util.ChronoField
import org.opencds.cqf.cql.engine.util.ChronoUnit
import org.opencds.cqf.cql.engine.util.LocalDate
import org.opencds.cqf.cql.engine.util.OffsetDateTime
import org.opencds.cqf.cql.engine.util.ZoneOffset

@Suppress("MagicNumber")
object TemporalHelper {
    fun normalizeDateTimeElements(vararg elements: Int): Array<String?> {
        val ret = arrayOfNulls<String>(elements.size)
        for (i in elements.indices) {
            when (i) {
                0 -> ret[i] = addLeadingZeroes(elements[i], 4)
                6 -> ret[i] = addLeadingZeroes(elements[i], 3)
                else -> ret[i] = addLeadingZeroes(elements[i], 2)
            }
        }

        return ret
    }

    fun normalizeTimeElements(vararg elements: Int): Array<String?> {
        val ret = arrayOfNulls<String>(elements.size)
        for (i in elements.indices) {
            when (i) {
                3 -> ret[i] = addLeadingZeroes(elements[i], 3)
                else -> ret[i] = addLeadingZeroes(elements[i], 2)
            }
        }

        return ret
    }

    fun addLeadingZeroes(element: Int, length: Int): String {
        val strElement = element.toString()
        return "0".repeat(length - strElement.length) + strElement
    }

    fun autoCompleteDateTimeString(dateString: String, precision: Precision): String {
        return when (precision) {
            Precision.YEAR -> dateString + "-01-01T00:00:00.000"
            Precision.MONTH -> dateString + "-01T00:00:00.000"
            Precision.DAY -> dateString + "T00:00:00.000"
            Precision.HOUR -> dateString + ":00:00.000"
            Precision.MINUTE -> dateString + ":00.000"
            Precision.SECOND -> dateString + ".000"
            else -> dateString
        }
    }

    fun autoCompleteDateString(dateString: String, precision: Precision): String {
        return when (precision) {
            Precision.YEAR -> dateString + "-01-01"
            Precision.MONTH -> dateString + "-01"
            else -> dateString
        }
    }

    fun autoCompleteTimeString(timeString: String, precision: Precision): String {
        return when (precision) {
            Precision.HOUR,
            Precision.MINUTE -> timeString + ":00.000"

            Precision.SECOND -> timeString + ".000"
            else -> timeString
        }
    }

    fun cleanArray(vararg elements: Int?): IntArray {
        return elements.filter { obj -> obj != null }.map { obj -> obj!! }.toIntArray()
    }

    @JvmStatic
    fun zoneToOffset(zone: ZoneOffset): BigDecimal {
        val seconds = zone.get(ChronoField.OFFSET_SECONDS)
        return BigDecimal((seconds / 60f / 60f).toDouble().toString())
    }

    fun weeksToDays(weeks: Int): Int {
        var weeks = weeks
        var years = 0
        if (weeks >= 52) {
            years = (weeks / 52)
            weeks -= years * 52
        }
        return weeks * 7 + (years * 365)
    }

    fun truncateValueToTargetPrecision(
        value: Long,
        precision: Precision,
        targetPrecision: Precision?,
    ): Long {
        when (targetPrecision) {
            Precision.YEAR -> {
                return when (precision) {
                    Precision.YEAR -> value
                    Precision.MONTH -> value / 12
                    Precision.WEEK -> 0
                    Precision.DAY -> value / 365
                    Precision.HOUR -> value / (24 * 365)
                    Precision.MINUTE -> value / (24 * 365 * 60)
                    Precision.SECOND -> value / (24 * 365 * 60 * 60)
                    Precision.MILLISECOND -> ((value / 1000) / (3600)) / (24 * 365)
                }
            }
            Precision.MONTH -> {
                return when (precision) {
                    Precision.YEAR -> value * 12
                    Precision.MONTH -> value
                    Precision.WEEK -> 0
                    Precision.DAY -> value / 30
                    Precision.HOUR -> value / (30 * 24)
                    Precision.MINUTE -> value / (30 * 24 * 60)
                    Precision.SECOND -> value / (30 * 24 * 60 * 60)
                    Precision.MILLISECOND -> ((value / 1000) / (3600)) / (30 * 24)
                }
            }
            Precision.DAY -> {
                return when (precision) {
                    Precision.YEAR -> value * 365
                    Precision.MONTH -> value * 12
                    Precision.WEEK -> 0
                    Precision.DAY -> value
                    Precision.HOUR -> value / 24
                    Precision.MINUTE -> value / (24 * 60)
                    Precision.SECOND -> value / (24 * 60 * 60)
                    Precision.MILLISECOND -> ((value / 1000) / (3600)) / 24
                }
            }
            Precision.HOUR -> {
                return when (precision) {
                    Precision.YEAR -> value * 365 * 24
                    Precision.MONTH -> value * 30 * 24
                    Precision.WEEK -> 0
                    Precision.DAY -> value * 24
                    Precision.HOUR -> value
                    Precision.MINUTE -> value / 60
                    Precision.SECOND -> value / (60 * 60)
                    Precision.MILLISECOND -> (value / 1000) / 3600
                }
            }
            Precision.MINUTE -> {
                return when (precision) {
                    Precision.YEAR -> value * 365 * 24 * 60
                    Precision.MONTH -> value * 30 * 24 * 60
                    Precision.WEEK -> 0
                    Precision.DAY -> value * 24 * 60
                    Precision.HOUR -> value * 60
                    Precision.MINUTE -> value
                    Precision.SECOND -> value / 60
                    Precision.MILLISECOND -> (value / 1000) / 60
                }
            }
            Precision.SECOND -> {
                return when (precision) {
                    Precision.YEAR -> value * 365 * 24 * 60 * 60
                    Precision.MONTH -> value * 30 * 24 * 60 * 60
                    Precision.WEEK -> 0
                    Precision.DAY -> value * 24 * 60 * 60
                    Precision.HOUR -> value * 60 * 60
                    Precision.MINUTE -> value * 60
                    Precision.SECOND -> value
                    Precision.MILLISECOND -> value / 1000
                }
            }
            Precision.MILLISECOND -> {
                return when (precision) {
                    Precision.YEAR -> value * 365 * 24 * 60 * 60 * 1000
                    Precision.MONTH -> value * 30 * 24 * 60 * 60
                    Precision.WEEK -> 0
                    Precision.DAY -> value * 24 * 60 * 60
                    Precision.HOUR -> value * 60 * 60
                    Precision.MINUTE -> value * 60
                    Precision.SECOND -> value
                    Precision.MILLISECOND -> value / 1000
                }
            }
            else -> return 0
        }
    }

    /**
     * Truncates the OffsetDateTime to the specified precision.
     *
     * @param dateTime the OffsetDateTime to truncate
     * @param precision the precision to truncate to
     * @return the truncated OffsetDateTime
     */
    @JvmStatic
    fun truncateToPrecision(dateTime: OffsetDateTime, precision: Precision): OffsetDateTime {
        return when (precision) {
            Precision.YEAR -> dateTime.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS)
            Precision.MONTH -> dateTime.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
            Precision.WEEK -> dateTime.truncatedTo(ChronoUnit.DAYS)
            else -> dateTime.truncatedTo(precision.toChronoUnit())
        }
    }

    /**
     * Truncates the LocalDate to the specified precision.
     *
     * @param date the LocalDate to truncate
     * @param precision the precision to truncate to
     * @return the truncated LocalDate
     */
    @JvmStatic
    fun truncateToPrecision(date: LocalDate, precision: Precision): LocalDate {
        return when (precision) {
            Precision.YEAR -> date.withDayOfYear(1)
            Precision.MONTH -> date.withDayOfMonth(1)
            else -> date
        }
    }
}
