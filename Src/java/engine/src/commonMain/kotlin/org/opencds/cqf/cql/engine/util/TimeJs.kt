package org.opencds.cqf.cql.engine.util

import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.number
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.yearsUntil

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

private fun monthLengthInDays(year: Int, month: Int): Int {
    return when (month) {
        2 -> if (isLeapYear(year)) 29 else 28
        4,
        6,
        9,
        11 -> 30
        else -> 31
    }
}

data class DateJs(internal val instant: kotlin.time.Instant) {
    fun after(that: DateJs): Boolean {
        return this.instant > that.instant
    }
}

fun dateFromJs(instant: InstantJs): DateJs {
    return DateJs(instant.instant)
}

interface TemporalFieldJs

interface TemporalUnitJs

enum class ChronoFieldJs : TemporalFieldJs {
    YEAR,
    MONTH_OF_YEAR,
    DAY_OF_MONTH,
    HOUR_OF_DAY,
    MINUTE_OF_HOUR,
    SECOND_OF_MINUTE,
    MILLI_OF_SECOND,
    OFFSET_SECONDS,
}

interface TemporalJs

enum class ChronoUnitJs : TemporalUnitJs {
    YEARS,
    MONTHS,
    DAYS,
    HOURS,
    MINUTES,
    SECONDS,
    MILLIS;

    fun between(start: TemporalJs, end: TemporalJs): Long {
        if (start is LocalDateJs && end is LocalDateJs) {
            return when (this) {
                YEARS -> start.value.yearsUntil(end.value).toLong()
                MONTHS -> start.value.monthsUntil(end.value).toLong()
                DAYS -> start.value.daysUntil(end.value).toLong()
                else -> throw IllegalArgumentException("Unsupported unit for LocalDate: $this")
            }
        }
        if (start is OffsetDateTimeJs && end is OffsetDateTimeJs) {
            val diff = end.instant - start.instant
            return when (this) {
                YEARS -> start.instant.yearsUntil(end.instant, start.offset.asTimeZone()).toLong()
                MONTHS -> start.instant.monthsUntil(end.instant, start.offset.asTimeZone()).toLong()
                DAYS -> start.instant.daysUntil(end.instant, start.offset.asTimeZone()).toLong()
                HOURS -> diff.inWholeHours
                MINUTES -> diff.inWholeMinutes
                SECONDS -> diff.inWholeSeconds
                MILLIS -> diff.inWholeMilliseconds
            }
        }
        if (start is LocalTimeJs && end is LocalTimeJs) {
            val diffNs = end.value.toNanosecondOfDay() - start.value.toNanosecondOfDay()
            return when (this) {
                HOURS -> diffNs / 3600_000_000_000L
                MINUTES -> diffNs / 60_000_000_000L
                SECONDS -> diffNs / 1_000_000_000L
                MILLIS -> diffNs / 1_000_000L
                else -> throw IllegalArgumentException("Unsupported unit for LocalTime: $this")
            }
        }
        if (start is LocalDateTimeJs && end is LocalDateTimeJs) {
            val startInstant = start.localDateTime.toInstant(kotlinx.datetime.TimeZone.UTC)
            val endInstant = end.localDateTime.toInstant(kotlinx.datetime.TimeZone.UTC)
            val diff = endInstant - startInstant

            return when (this) {
                YEARS -> startInstant.yearsUntil(endInstant, kotlinx.datetime.TimeZone.UTC).toLong()
                MONTHS ->
                    startInstant.monthsUntil(endInstant, kotlinx.datetime.TimeZone.UTC).toLong()
                DAYS -> startInstant.daysUntil(endInstant, kotlinx.datetime.TimeZone.UTC).toLong()
                HOURS -> diff.inWholeHours
                MINUTES -> diff.inWholeMinutes
                SECONDS -> diff.inWholeSeconds
                MILLIS -> diff.inWholeMilliseconds
            }
        }
        throw IllegalArgumentException(
            "Unsupported Temporal types: ${start::class} and ${end::class}"
        )
    }

    internal fun toDateTimeUnit(): DateTimeUnit {
        return when (this) {
            YEARS -> DateTimeUnit.YEAR
            MONTHS -> DateTimeUnit.MONTH
            DAYS -> DateTimeUnit.DAY
            HOURS -> DateTimeUnit.HOUR
            MINUTES -> DateTimeUnit.MINUTE
            SECONDS -> DateTimeUnit.SECOND
            MILLIS -> DateTimeUnit.MILLISECOND
        }
    }
}

data class ValueRangeJs(private val min: Long, private val max: Long) {
    fun getMinimum(): Long {
        return min
    }

    fun getMaximum(): Long {
        return max
    }
}

data class LocalDateJs(internal val value: kotlinx.datetime.LocalDate) : TemporalJs {
    fun get(field: TemporalFieldJs): Int {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.YEAR -> value.year
            ChronoFieldJs.MONTH_OF_YEAR -> value.month.number
            ChronoFieldJs.DAY_OF_MONTH -> value.dayOfMonth
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
    }

    fun getYear(): Int {
        return value.year
    }

    fun getMonthValue(): Int {
        return value.month.number
    }

    fun getDayOfMonth(): Int {
        return value.dayOfMonth
    }

    fun withDayOfYear(dayOfYear: Int): LocalDateJs {
        val d = kotlinx.datetime.LocalDate(value.year, 1, 1).plus(dayOfYear - 1, DateTimeUnit.DAY)
        return LocalDateJs(d)
    }

    fun withDayOfMonth(dayOfMonth: Int): LocalDateJs {
        return LocalDateJs(kotlinx.datetime.LocalDate(value.year, value.month.number, dayOfMonth))
    }

    fun with(field: TemporalFieldJs, newValue: Long): LocalDateJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        var y = value.year
        var m = value.month.number
        var d = value.dayOfMonth
        when (field) {
            ChronoFieldJs.YEAR -> y = newValue.toInt()
            ChronoFieldJs.MONTH_OF_YEAR -> m = newValue.toInt()
            ChronoFieldJs.DAY_OF_MONTH -> d = newValue.toInt()
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
        val maxDay = monthLengthInDays(y, m)
        if (d > maxDay) d = maxDay
        return LocalDateJs(kotlinx.datetime.LocalDate(y, m, d))
    }

    fun range(field: TemporalFieldJs): ValueRangeJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.YEAR -> ValueRangeJs(Long.MIN_VALUE, Long.MAX_VALUE)
            ChronoFieldJs.MONTH_OF_YEAR -> ValueRangeJs(1, 12)
            ChronoFieldJs.DAY_OF_MONTH ->
                ValueRangeJs(1, monthLengthInDays(value.year, value.monthNumber).toLong())
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
    }

    fun plus(amountToAdd: Long, field: TemporalUnitJs): LocalDateJs {
        require(field is ChronoUnitJs) { "Unsupported unit type: ${field::class}" }
        val res =
            when (field) {
                ChronoUnitJs.YEARS -> value.plus(amountToAdd.toInt(), DateTimeUnit.YEAR)
                ChronoUnitJs.MONTHS -> value.plus(amountToAdd.toInt(), DateTimeUnit.MONTH)
                ChronoUnitJs.DAYS -> value.plus(amountToAdd.toInt(), DateTimeUnit.DAY)
                else -> throw IllegalArgumentException("Unsupported unit: $field")
            }
        return LocalDateJs(res)
    }

    fun plusYears(years: Long): LocalDateJs {
        return plus(years, ChronoUnitJs.YEARS)
    }

    fun minus(amountToSubtract: Long, field: TemporalUnitJs): LocalDateJs {
        return plus(-amountToSubtract, field)
    }

    fun atStartOfDay(zone: ZoneIdJs): ZonedDateTimeJs {
        val ldt = kotlinx.datetime.LocalDateTime(value, kotlinx.datetime.LocalTime(0, 0))
        val instant = ldt.toInstant(zone.zone)
        return ZonedDateTimeJs(instant, zone.zone)
    }

    override fun toString(): String {
        return value.toString()
    }
}

fun localDateOfJs(year: Int, month: Int, dayOfMonth: Int): LocalDateJs {
    return LocalDateJs(kotlinx.datetime.LocalDate(year, month, dayOfMonth))
}

fun localDateParseJs(text: String): LocalDateJs {
    return LocalDateJs(kotlinx.datetime.LocalDate.parse(text))
}

data class ZoneOffsetJs(internal val offset: UtcOffset) : ZoneIdJs(offset.asTimeZone()) {
    fun getTotalSeconds(): Int {
        return offset.totalSeconds
    }

    fun get(field: TemporalFieldJs): Int {
        if (field == ChronoFieldJs.OFFSET_SECONDS) {
            return offset.totalSeconds
        }
        throw IllegalArgumentException("Unsupported field: $field")
    }

    fun getId(): String {
        return offset.toString()
    }
}

fun zoneOffsetOfHoursMinutesJs(hours: Int, minutes: Int): ZoneOffsetJs {
    return ZoneOffsetJs(UtcOffset(hours = hours, minutes = minutes))
}

abstract class TimeZoneJs(private val timeZone: kotlinx.datetime.TimeZone) {
    fun toZoneId(): ZoneIdJs {
        return object : ZoneIdJs(timeZone) {}
    }
}

fun timeZoneGetDefaultJs(): TimeZoneJs {
    return object : TimeZoneJs(kotlinx.datetime.TimeZone.currentSystemDefault()) {}
}

abstract class CalendarJs(
    var instant: kotlinx.datetime.Instant = Clock.System.now(),
    var timeZone: kotlinx.datetime.TimeZone = kotlinx.datetime.TimeZone.currentSystemDefault(),
) {
    fun setTime(date: DateJs) {
        instant = date.instant
    }

    fun toInstant(): InstantJs {
        return InstantJs(instant)
    }

    fun getTimeZone(): TimeZoneJs {
        return object : TimeZoneJs(timeZone) {}
    }
}

fun calendarGetInstanceJs(): CalendarJs {
    return object : CalendarJs() {}
}

data class OffsetDateTimeJs(
    internal val instant: kotlin.time.Instant,
    internal val offset: UtcOffset,
) : TemporalJs {
    val localDateTime: kotlinx.datetime.LocalDateTime
        get() = instant.toLocalDateTime(offset.asTimeZone())

    fun getYear(): Int {
        return localDateTime.year
    }

    fun getMonthValue(): Int {
        return localDateTime.month.number
    }

    fun getDayOfMonth(): Int {
        return localDateTime.dayOfMonth
    }

    fun getHour(): Int {
        return localDateTime.hour
    }

    fun getMinute(): Int {
        return localDateTime.minute
    }

    fun getSecond(): Int {
        return localDateTime.second
    }

    fun getOffset(): ZoneOffsetJs {
        return ZoneOffsetJs(offset)
    }

    fun get(field: TemporalFieldJs): Int {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.YEAR -> localDateTime.year
            ChronoFieldJs.MONTH_OF_YEAR -> localDateTime.month.number
            ChronoFieldJs.DAY_OF_MONTH -> localDateTime.dayOfMonth
            ChronoFieldJs.HOUR_OF_DAY -> localDateTime.hour
            ChronoFieldJs.MINUTE_OF_HOUR -> localDateTime.minute
            ChronoFieldJs.SECOND_OF_MINUTE -> localDateTime.second
            ChronoFieldJs.MILLI_OF_SECOND -> localDateTime.nanosecond / 1_000_000
            ChronoFieldJs.OFFSET_SECONDS -> offset.totalSeconds
        }
    }

    fun plus(amountToAdd: Long, unit: TemporalUnitJs): OffsetDateTimeJs {
        require(unit is ChronoUnitJs) { "Unsupported unit type: ${unit::class}" }
        return OffsetDateTimeJs(
            instant.plus(amountToAdd, unit.toDateTimeUnit(), offset.asTimeZone()),
            offset,
        )
    }

    fun plusYears(years: Long): OffsetDateTimeJs {
        return plus(years, ChronoUnitJs.YEARS)
    }

    fun minus(amountToSubtract: Long, unit: TemporalUnitJs): OffsetDateTimeJs {
        require(unit is ChronoUnitJs) { "Unsupported unit type: ${unit::class}" }
        return OffsetDateTimeJs(
            instant.minus(amountToSubtract, unit.toDateTimeUnit(), offset.asTimeZone()),
            offset,
        )
    }

    fun with(field: TemporalFieldJs, newValue: Long): OffsetDateTimeJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        if (field == ChronoFieldJs.OFFSET_SECONDS) {
            val newOffset = UtcOffset(seconds = newValue.toInt())
            val newInstant = localDateTime.toInstant(newOffset.asTimeZone())
            return OffsetDateTimeJs(newInstant, newOffset)
        }
        var y = localDateTime.year
        var m = localDateTime.month.number
        var d = localDateTime.dayOfMonth
        var h = localDateTime.hour
        var min = localDateTime.minute
        var s = localDateTime.second
        var ns = localDateTime.nanosecond
        when (field) {
            ChronoFieldJs.YEAR -> y = newValue.toInt()
            ChronoFieldJs.MONTH_OF_YEAR -> m = newValue.toInt()
            ChronoFieldJs.DAY_OF_MONTH -> d = newValue.toInt()
            ChronoFieldJs.HOUR_OF_DAY -> h = newValue.toInt()
            ChronoFieldJs.MINUTE_OF_HOUR -> min = newValue.toInt()
            ChronoFieldJs.SECOND_OF_MINUTE -> s = newValue.toInt()
            ChronoFieldJs.MILLI_OF_SECOND -> ns = newValue.toInt() * 1_000_000
        }
        val maxDay = monthLengthInDays(y, m)
        if (d > maxDay) d = maxDay
        val newLdt = kotlinx.datetime.LocalDateTime(y, m, d, h, min, s, ns)
        return OffsetDateTimeJs(newLdt.toInstant(offset.asTimeZone()), offset)
    }

    fun withDayOfYear(dayOfYear: Int): OffsetDateTimeJs {
        val newLd =
            kotlinx.datetime
                .LocalDate(localDateTime.year, 1, 1)
                .plus(dayOfYear - 1, DateTimeUnit.DAY)
        val newLdt = kotlinx.datetime.LocalDateTime(newLd, localDateTime.time)
        return OffsetDateTimeJs(newLdt.toInstant(offset.asTimeZone()), offset)
    }

    fun withDayOfMonth(dayOfMonth: Int): OffsetDateTimeJs {
        val newLdt =
            kotlinx.datetime.LocalDateTime(
                localDateTime.year,
                localDateTime.monthNumber,
                dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                localDateTime.second,
                localDateTime.nanosecond,
            )
        return OffsetDateTimeJs(newLdt.toInstant(offset.asTimeZone()), offset)
    }

    fun withOffsetSameInstant(offset: ZoneOffsetJs): OffsetDateTimeJs {
        return OffsetDateTimeJs(instant, offset.offset)
    }

    fun truncatedTo(unit: TemporalUnitJs): OffsetDateTimeJs {
        require(unit is ChronoUnitJs) { "Unsupported unit type: ${unit::class}" }
        val newLdt =
            when (unit) {
                ChronoUnitJs.DAYS ->
                    kotlinx.datetime.LocalDateTime(
                        localDateTime.date,
                        kotlinx.datetime.LocalTime(0, 0),
                    )
                ChronoUnitJs.HOURS ->
                    kotlinx.datetime.LocalDateTime(
                        localDateTime.date,
                        kotlinx.datetime.LocalTime(localDateTime.hour, 0),
                    )
                ChronoUnitJs.MINUTES ->
                    kotlinx.datetime.LocalDateTime(
                        localDateTime.date,
                        kotlinx.datetime.LocalTime(localDateTime.hour, localDateTime.minute),
                    )
                ChronoUnitJs.SECONDS ->
                    kotlinx.datetime.LocalDateTime(
                        localDateTime.date,
                        kotlinx.datetime.LocalTime(
                            localDateTime.hour,
                            localDateTime.minute,
                            localDateTime.second,
                        ),
                    )
                ChronoUnitJs.MILLIS ->
                    kotlinx.datetime.LocalDateTime(
                        localDateTime.date,
                        kotlinx.datetime.LocalTime(
                            localDateTime.hour,
                            localDateTime.minute,
                            localDateTime.second,
                            (localDateTime.nanosecond / 1_000_000) * 1_000_000,
                        ),
                    )
                else -> localDateTime
            }
        return OffsetDateTimeJs(newLdt.toInstant(offset.asTimeZone()), offset)
    }

    fun toInstant(): InstantJs {
        return InstantJs(instant)
    }

    fun range(field: TemporalFieldJs): ValueRangeJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.YEAR -> ValueRangeJs(Long.MIN_VALUE, Long.MAX_VALUE)
            ChronoFieldJs.MONTH_OF_YEAR -> ValueRangeJs(1, 12)
            ChronoFieldJs.DAY_OF_MONTH ->
                ValueRangeJs(
                    1,
                    monthLengthInDays(localDateTime.year, localDateTime.monthNumber).toLong(),
                )
            ChronoFieldJs.HOUR_OF_DAY -> ValueRangeJs(0, 23)
            ChronoFieldJs.MINUTE_OF_HOUR -> ValueRangeJs(0, 59)
            ChronoFieldJs.SECOND_OF_MINUTE -> ValueRangeJs(0, 59)
            ChronoFieldJs.MILLI_OF_SECOND -> ValueRangeJs(0, 999)
            ChronoFieldJs.OFFSET_SECONDS -> ValueRangeJs(-64800, 64800)
        }
    }

    fun toLocalDate(): LocalDateJs {
        return LocalDateJs(localDateTime.date)
    }

    fun toLocalDateTime(): LocalDateTimeJs {
        return LocalDateTimeJs(localDateTime)
    }

    override fun toString(): String {
        return dateTimeFormatterIsoOffsetDateTimeFormatJs(this)
    }
}

fun offsetDateTimeOfJs(localDateTime: LocalDateTimeJs, zoneOffset: ZoneOffsetJs): OffsetDateTimeJs {
    val instant = localDateTime.localDateTime.toInstant(zoneOffset.offset.asTimeZone())
    return OffsetDateTimeJs(instant, zoneOffset.offset)
}

data class LocalDateTimeJs(internal val localDateTime: kotlinx.datetime.LocalDateTime) :
    TemporalJs {
    fun getYear(): Int {
        return localDateTime.year
    }

    fun getMonthValue(): Int {
        return localDateTime.month.number
    }

    fun getDayOfMonth(): Int {
        return localDateTime.dayOfMonth
    }

    fun getHour(): Int {
        return localDateTime.hour
    }

    fun getMinute(): Int {
        return localDateTime.minute
    }

    fun getSecond(): Int {
        return localDateTime.second
    }

    fun minusSeconds(seconds: Long): LocalDateTimeJs {
        val nextInstant =
            localDateTime
                .toInstant(kotlinx.datetime.TimeZone.UTC)
                .minus(seconds, DateTimeUnit.SECOND)
        return LocalDateTimeJs(nextInstant.toLocalDateTime(kotlinx.datetime.TimeZone.UTC))
    }
}

data class InstantJs(internal val instant: kotlin.time.Instant)

fun localDateTimeOfJs(
    year: Int,
    month: Int,
    dayOfMonth: Int,
    hour: Int,
    minute: Int,
    second: Int,
): LocalDateTimeJs {
    return LocalDateTimeJs(
        kotlinx.datetime.LocalDateTime(year, month, dayOfMonth, hour, minute, second)
    )
}

fun offsetDateTimeParseJs(text: String): OffsetDateTimeJs {
    val instant = kotlin.time.Instant.parse(text)
    val tIndex = text.indexOf('T')
    val lastPlus = text.lastIndexOf('+')
    val lastMinus = text.lastIndexOf('-')
    val zIndex = text.lastIndexOf('Z')
    val index = maxOf(lastPlus, maxOf(lastMinus, zIndex))

    val offsetStr = if (tIndex != -1 && index > tIndex) text.substring(index) else "Z"
    val offset = if (offsetStr == "Z") UtcOffset.ZERO else UtcOffset.parse(offsetStr)
    return OffsetDateTimeJs(instant, offset)
}

fun offsetDateTimeOfInstantJs(instant: InstantJs, offset: ZoneIdJs): OffsetDateTimeJs {
    val i = instant.instant
    val z = offset.zone
    return OffsetDateTimeJs(i, z.offsetAt(i))
}

fun dateTimeFormatterIsoOffsetDateTimeFormatJs(dateTime: OffsetDateTimeJs): String {
    val ldt = dateTime.instant.toLocalDateTime(dateTime.offset.asTimeZone())
    val offsetStr = if (dateTime.offset == UtcOffset.ZERO) "Z" else dateTime.offset.toString()
    return ldt.toString() + offsetStr
}

public abstract class ZoneIdJs(internal val zone: kotlinx.datetime.TimeZone)

fun zoneIdOfJs(zoneId: String): ZoneIdJs {
    return object : ZoneIdJs(kotlinx.datetime.TimeZone.of(zoneId)) {}
}

fun zoneIdToZoneOffsetJs(zoneId: ZoneIdJs, localDateTime: LocalDateTimeJs): ZoneOffsetJs {
    return ZoneOffsetJs(
        zoneId.zone.offsetAt(localDateTime.localDateTime.toInstant(kotlinx.datetime.TimeZone.UTC))
    )
}

data class ZonedDateTimeJs(
    private val instant: kotlin.time.Instant,
    private val zone: kotlinx.datetime.TimeZone,
) {
    fun getZone(): ZoneIdJs {
        return object : ZoneIdJs(zone) {}
    }

    fun getOffset(): ZoneOffsetJs {
        return ZoneOffsetJs(zone.offsetAt(instant))
    }

    fun toInstant(): InstantJs {
        return InstantJs(instant)
    }

    fun toOffsetDateTime(): OffsetDateTimeJs {
        return OffsetDateTimeJs(instant, zone.offsetAt(instant))
    }
}

fun zonedDateTimeNowJs(): ZonedDateTimeJs {
    return ZonedDateTimeJs(Clock.System.now(), kotlinx.datetime.TimeZone.currentSystemDefault())
}

data class LocalTimeJs(internal val value: kotlinx.datetime.LocalTime) : TemporalJs {
    fun getHour(): Int {
        return value.hour
    }

    fun getMinute(): Int {
        return value.minute
    }

    fun getSecond(): Int {
        return value.second
    }

    fun get(field: TemporalFieldJs): Int {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.HOUR_OF_DAY -> value.hour
            ChronoFieldJs.MINUTE_OF_HOUR -> value.minute
            ChronoFieldJs.SECOND_OF_MINUTE -> value.second
            ChronoFieldJs.MILLI_OF_SECOND -> value.nanosecond / 1_000_000
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
    }

    fun plusHours(hours: Long): LocalTimeJs {
        return plus(hours, ChronoUnitJs.HOURS)
    }

    fun with(field: TemporalFieldJs, newValue: Long): LocalTimeJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        var h = value.hour
        var m = value.minute
        var s = value.second
        var ns = value.nanosecond
        when (field) {
            ChronoFieldJs.HOUR_OF_DAY -> h = newValue.toInt()
            ChronoFieldJs.MINUTE_OF_HOUR -> m = newValue.toInt()
            ChronoFieldJs.SECOND_OF_MINUTE -> s = newValue.toInt()
            ChronoFieldJs.MILLI_OF_SECOND -> ns = newValue.toInt() * 1_000_000
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
        return LocalTimeJs(kotlinx.datetime.LocalTime(h, m, s, ns))
    }

    fun range(field: TemporalFieldJs): ValueRangeJs {
        require(field is ChronoFieldJs) { "Unsupported field type: ${field::class}" }
        return when (field) {
            ChronoFieldJs.HOUR_OF_DAY -> ValueRangeJs(0, 23)
            ChronoFieldJs.MINUTE_OF_HOUR -> ValueRangeJs(0, 59)
            ChronoFieldJs.SECOND_OF_MINUTE -> ValueRangeJs(0, 59)
            ChronoFieldJs.MILLI_OF_SECOND -> ValueRangeJs(0, 999)
            else -> throw IllegalArgumentException("Unsupported field: $field")
        }
    }

    fun truncatedTo(unit: TemporalUnitJs): LocalTimeJs {
        require(unit is ChronoUnitJs) { "Unsupported unit: ${unit::class}" }
        return when (unit) {
            ChronoUnitJs.HOURS -> LocalTimeJs(kotlinx.datetime.LocalTime(value.hour, 0))
            ChronoUnitJs.MINUTES ->
                LocalTimeJs(kotlinx.datetime.LocalTime(value.hour, value.minute))
            ChronoUnitJs.SECONDS ->
                LocalTimeJs(kotlinx.datetime.LocalTime(value.hour, value.minute, value.second))
            ChronoUnitJs.MILLIS ->
                LocalTimeJs(
                    kotlinx.datetime.LocalTime(
                        value.hour,
                        value.minute,
                        value.second,
                        (value.nanosecond / 1_000_000) * 1_000_000,
                    )
                )
            else -> this
        }
    }

    fun plus(amountToAdd: Long, unit: TemporalUnitJs): LocalTimeJs {
        require(unit is ChronoUnitJs) { "Unsupported unit type: ${unit::class}" }
        val addNs =
            when (unit) {
                ChronoUnitJs.HOURS -> amountToAdd * 3600_000_000_000L
                ChronoUnitJs.MINUTES -> amountToAdd * 60_000_000_000L
                ChronoUnitJs.SECONDS -> amountToAdd * 1_000_000_000L
                ChronoUnitJs.MILLIS -> amountToAdd * 1_000_000L
                else -> 0L
            }
        val currentNs = value.toNanosecondOfDay()
        val maxNs = 86400_000_000_000L
        val totalNs = ((currentNs + addNs) % maxNs + maxNs) % maxNs

        return LocalTimeJs(kotlinx.datetime.LocalTime.fromNanosecondOfDay(totalNs))
    }

    fun minus(amountToSubtract: Long, unit: TemporalUnitJs): LocalTimeJs {
        return plus(-amountToSubtract, unit)
    }
}

fun localTimeParseJs(s: String): LocalTimeJs {
    return LocalTimeJs(kotlinx.datetime.LocalTime.parse(s))
}
