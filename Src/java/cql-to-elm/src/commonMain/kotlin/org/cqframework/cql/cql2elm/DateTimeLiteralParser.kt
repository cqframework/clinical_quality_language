package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.utils.isLeapYear
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Time

/**
 * Parses ISO 8601 date, time, and date/time string literals into ELM expressions.
 *
 * Factored out of [Cql2ElmVisitor] so the literal-parsing logic can be exercised and maintained
 * independently of parse-tree walking.
 */
@Suppress(
    "MagicNumber",
    "CyclomaticComplexMethod",
    "LongMethod",
    "NestedBlockDepth",
    "ReturnCount",
    "ComplexCondition",
    "TooGenericExceptionCaught",
    "UseRequire",
    "MaxLineLength",
)
class DateTimeLiteralParser(
    private val libraryBuilder: Cql2ElmContext,
    private val of: IdObjectFactory,
) {
    /**
     * Parse a date or date/time literal (with optional time and timezone components).
     *
     * Returns an ELM `Date` if no time component is present, otherwise a `DateTime`.
     */
    fun parseDateTime(input: String): Expression {
        /*
         * DATETIME
         * : '@'
         * [0-9][0-9][0-9][0-9] // year
         * (
         * (
         * '-'[0-9][0-9] // month
         * (
         * (
         * '-'[0-9][0-9] // day
         * ('T' TIMEFORMAT?)?
         * )
         * | 'T'
         * )?
         * )
         * | 'T'
         * )?
         * ('Z' | ('+' | '-') [0-9][0-9]':'[0-9][0-9])? // timezone offset
         * ;
         */
        val matcher = DATETIME_PATTERN.matchEntire(input)
        return if (matcher != null) {
            try {
                val result = of.createDateTime()
                val year = matcher.groups[1]!!.value.toInt()
                var month = -1
                var hour = -1
                result.year = libraryBuilder.createLiteral(year)
                if (matcher.groups[5] != null) {
                    month = matcher.groups[5]!!.value.toInt()
                    require(month in 0..12) { "Invalid month in date/time literal ($input)." }
                    result.month = libraryBuilder.createLiteral(month)
                }
                if (matcher.groups[9] != null) {
                    val day = matcher.groups[9]!!.value.toInt()
                    var maxDay = 31
                    when (month) {
                        2 -> maxDay = if (isLeapYear(year)) 29 else 28
                        4,
                        6,
                        9,
                        11 -> maxDay = 30
                    }
                    require(day in 0..maxDay) { "Invalid day in date/time literal ($input)." }
                    result.day = libraryBuilder.createLiteral(day)
                }
                if (matcher.groups[13] != null) {
                    hour = matcher.groups[13]!!.value.toInt()
                    require(hour in 0..24) { "Invalid hour in date/time literal ($input)." }
                    result.hour = libraryBuilder.createLiteral(hour)
                }
                if (matcher.groups[15] != null) {
                    val minute = matcher.groups[15]!!.value.toInt()
                    require(minute in 0..60 && !(hour == 24 && minute > 0)) {
                        "Invalid minute in date/time literal ($input)."
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.groups[17] != null) {
                    val second = matcher.groups[17]!!.value.toInt()
                    require(second in 0..60 && !(hour == 24 && second > 0)) {
                        "Invalid second in date/time literal ($input)."
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.groups[19] != null) {
                    val millisecond = matcher.groups[19]!!.value.toInt()
                    require(millisecond >= 0 && !(hour == 24 && millisecond > 0)) {
                        "Invalid millisecond in date/time literal ($input)."
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                if (matcher.groups[23] != null && (matcher.groups[23]!!.value == "Z")) {
                    result.timezoneOffset = libraryBuilder.createLiteral(0.0)
                }
                if (matcher.groups[25] != null) {
                    val offsetPolarity = if ((matcher.groups[25]!!.value == "+")) 1 else -1
                    if (matcher.groups[28] != null) {
                        val hourOffset = matcher.groups[26]!!.value.toInt()
                        require(hourOffset in 0..14) {
                            "Timezone hour offset is out of range in date/time literal ($input)."
                        }
                        val minuteOffset = matcher.groups[28]!!.value.toInt()
                        require(minuteOffset in 0..60 && !(hourOffset == 14 && minuteOffset > 0)) {
                            "Timezone minute offset is out of range in date/time literal ($input)."
                        }
                        result.timezoneOffset =
                            libraryBuilder.createLiteral(
                                (hourOffset + (minuteOffset.toDouble() / 60)) * offsetPolarity
                            )
                    } else {
                        if (matcher.groups[26] != null) {
                            val hourOffset = matcher.groups[26]!!.value.toInt()
                            require(hourOffset in 0..14) {
                                "Timezone hour offset is out of range in date/time literal ($input)."
                            }
                            result.timezoneOffset =
                                libraryBuilder.createLiteral(
                                    (hourOffset * offsetPolarity).toDouble()
                                )
                        }
                    }
                }
                if (
                    (result.hour == null) &&
                        (matcher.groups[11] == null) &&
                        (matcher.groups[20] == null) &&
                        (matcher.groups[21] == null)
                ) {
                    val date = of.createDate()
                    date.year = result.year
                    date.month = result.month
                    date.day = result.day
                    date.resultType = libraryBuilder.resolveTypeName("System", "Date")
                    return date
                }
                result.resultType = libraryBuilder.resolveTypeName("System", "DateTime")
                result
            } catch (e: RuntimeException) {
                throw IllegalArgumentException(
                    "Invalid date-time input ($input)." +
                        " Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|(+/-hh:mm)).",
                    e,
                )
            }
        } else {
            throw IllegalArgumentException(
                "Invalid date-time input ($input)." +
                    " Use ISO 8601 date time representation (yyyy-MM-ddThh:mm:ss.fff(Z|+/-hh:mm))."
            )
        }
    }

    /** Parse an ISO 8601 time literal (hh:mm:ss.fff). */
    @Suppress("TooGenericExceptionCaught")
    fun parseTime(input: String): Time {
        val matcher = TIME_PATTERN.matchEntire(input)
        return if (matcher != null) {
            try {
                val result = of.createTime()
                val hour = matcher.groups[1]!!.value.toInt()
                require(hour in 0..24) { "Invalid hour in time literal ($hour)." }
                result.hour = libraryBuilder.createLiteral(hour)
                if (matcher.groups[3] != null) {
                    val minute = matcher.groups[3]!!.value.toInt()
                    require(!((minute < 0) || (minute >= 60) || (hour == 24 && minute > 0))) {
                        "Invalid minute in time literal ($minute)."
                    }
                    result.minute = libraryBuilder.createLiteral(minute)
                }
                if (matcher.groups[5] != null) {
                    val second = matcher.groups[5]!!.value.toInt()
                    require(!((second < 0) || (second >= 60) || (hour == 24 && second > 0))) {
                        "Invalid second in time literal ($second)."
                    }
                    result.second = libraryBuilder.createLiteral(second)
                }
                if (matcher.groups[7] != null) {
                    val millisecond = matcher.groups[7]!!.value.toInt()
                    require(hour == 24 && millisecond == 0 || millisecond >= 0) {
                        "Invalid millisecond in time literal ($millisecond)."
                    }
                    result.millisecond = libraryBuilder.createLiteral(millisecond)
                }
                result.resultType = libraryBuilder.resolveTypeName("System", "Time")
                result
            } catch (e: RuntimeException) {
                throw IllegalArgumentException(
                    "Invalid time input ($input). Use ISO 8601 time representation (hh:mm:ss.fff).",
                    e,
                )
            }
        } else {
            throw IllegalArgumentException(
                "Invalid time input ($input). Use ISO 8601 time representation (hh:mm:ss.fff)."
            )
        }
    }

    companion object {
        private val DATETIME_PATTERN =
            Regex(
                "(\\d{4})(((-(\\d{2}))(((-(\\d{2}))((T)((\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?)?)?)|(T))?)|(T))?((Z)|(([+-])(\\d{2})(:(\\d{2}))))?"
            )

        private val TIME_PATTERN = Regex("T(\\d{2})(:(\\d{2})(:(\\d{2})(\\.(\\d+))?)?)?")
    }
}
