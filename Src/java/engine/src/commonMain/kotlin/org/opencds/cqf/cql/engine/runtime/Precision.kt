package org.opencds.cqf.cql.engine.runtime

import org.opencds.cqf.cql.engine.exception.InvalidPrecision
import org.opencds.cqf.cql.engine.util.ChronoField
import org.opencds.cqf.cql.engine.util.ChronoUnit

enum class Precision {
    YEAR,
    MONTH,
    WEEK,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    MILLISECOND;

    /**
     * If this precision is WEEK, returns DAY. Otherwise, returns the same precision.
     *
     * @return the remapped precision
     */
    fun weekAsDay(): Precision {
        return if (this == WEEK) DAY else this
    }

    fun toChronoField(): ChronoField {
        return when (this) {
            YEAR -> ChronoField.YEAR
            MONTH -> ChronoField.MONTH_OF_YEAR
            DAY -> ChronoField.DAY_OF_MONTH
            HOUR -> ChronoField.HOUR_OF_DAY
            MINUTE -> ChronoField.MINUTE_OF_HOUR
            SECOND -> ChronoField.SECOND_OF_MINUTE
            else -> ChronoField.MILLI_OF_SECOND
        }
    }

    fun toChronoUnit(): ChronoUnit {
        return when (this) {
            YEAR -> ChronoUnit.YEARS
            MONTH -> ChronoUnit.MONTHS
            DAY -> ChronoUnit.DAYS
            HOUR -> ChronoUnit.HOURS
            MINUTE -> ChronoUnit.MINUTES
            SECOND -> ChronoUnit.SECONDS
            else -> ChronoUnit.MILLIS
        }
    }

    fun toDateIndex(): Int {
        return when (this) {
            YEAR -> 0
            MONTH -> 1
            else -> 2
        }
    }

    fun toDateTimeIndex(): Int {
        return when (this) {
            YEAR -> 0
            MONTH -> 1
            DAY -> 2
            HOUR -> 3
            MINUTE -> 4
            SECOND -> 5
            else -> 6
        }
    }

    fun toTimeIndex(): Int {
        return when (this) {
            HOUR -> 0
            MINUTE -> 1
            SECOND -> 2
            else -> 3
        }
    }

    val nextPrecision: Precision
        get() {
            return when (this) {
                YEAR -> MONTH
                MONTH -> DAY
                DAY -> HOUR
                HOUR -> MINUTE
                MINUTE -> SECOND
                SECOND -> MILLISECOND
                else -> MILLISECOND
            }
        }

    override fun toString(): String {
        return when (this) {
            YEAR -> "year"
            MONTH -> "month"
            WEEK -> "week"
            DAY -> "day"
            HOUR -> "hour"
            MINUTE -> "minute"
            SECOND -> "second"
            else -> "millisecond"
        }
    }

    companion object {
        fun fromString(precision: String): Precision {
            var precision = precision
            precision = precision.lowercase()
            if (precision.startsWith("year")) {
                return YEAR
            } else if (precision.startsWith("month")) {
                return MONTH
            } else if (precision.startsWith("day")) {
                return DAY
            } else if (precision.startsWith("week")) {
                return WEEK
            } else if (precision.startsWith("hour")) {
                return HOUR
            } else if (precision.startsWith("minute")) {
                return MINUTE
            } else if (precision.startsWith("second")) {
                return SECOND
            } else if (precision.startsWith("millisecond")) {
                return MILLISECOND
            }

            throw InvalidPrecision("Invalid precision: $precision")
        }

        fun fromDateIndex(index: Int): Precision {
            return when (index) {
                0 -> YEAR
                1 -> MONTH
                2 -> DAY
                else -> throw InvalidPrecision("Invalid precision index: $index")
            }
        }

        fun fromDateTimeIndex(index: Int): Precision {
            return when (index) {
                0 -> YEAR
                1 -> MONTH
                2 -> DAY
                3 -> HOUR
                4 -> MINUTE
                5 -> SECOND
                6 -> MILLISECOND
                else -> throw InvalidPrecision("Invalid precision index: $index")
            }
        }

        fun fromTimeIndex(index: Int): Precision {
            return fromDateTimeIndex(index + 3)
        }

        fun getDateChronoFieldFromIndex(index: Int): ChronoField {
            return when (index) {
                0 -> ChronoField.YEAR
                1 -> ChronoField.MONTH_OF_YEAR
                2 -> ChronoField.DAY_OF_MONTH
                else -> throw InvalidPrecision("Invalid precision index: $index")
            }
        }

        fun getDateTimeChronoFieldFromIndex(index: Int): ChronoField {
            return when (index) {
                0 -> ChronoField.YEAR
                1 -> ChronoField.MONTH_OF_YEAR
                2 -> ChronoField.DAY_OF_MONTH
                3 -> ChronoField.HOUR_OF_DAY
                4 -> ChronoField.MINUTE_OF_HOUR
                5 -> ChronoField.SECOND_OF_MINUTE
                6 -> ChronoField.MILLI_OF_SECOND
                else -> throw InvalidPrecision("Invalid precision index: $index")
            }
        }

        fun getTimeChronoFieldFromIndex(index: Int): ChronoField {
            return getDateTimeChronoFieldFromIndex(index + 3)
        }

        fun getLowestDatePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toDateIndex() < p2.toDateIndex()) p1 else p2
        }

        fun getHighestDatePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toDateIndex() > p2.toDateIndex()) p1 else p2
        }

        fun getLowestDateTimePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toDateTimeIndex() < p2.toDateTimeIndex()) p1 else p2
        }

        fun getHighestDateTimePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toDateTimeIndex() > p2.toDateTimeIndex()) p1 else p2
        }

        fun getLowestTimePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toTimeIndex() < p2.toTimeIndex()) p1 else p2
        }

        fun getHighestTimePrecision(p1: Precision, p2: Precision): Precision {
            return if (p1.toTimeIndex() > p2.toTimeIndex()) p1 else p2
        }
    }
}
