package org.opencds.cqf.cql.engine.util

import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class DateJs {
    fun after(that: Date): Boolean {
        TODO()
    }
}

fun dateFromJs(instant: Instant): Date {
    TODO()
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

    fun between(start: Temporal, end: Temporal): Long {
        TODO()
    }

    fun toDateTimeUnit(): DateTimeUnit {
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

class ValueRangeJs {
    fun getMinimum(): Long {
        TODO()
    }

    fun getMaximum(): Long {
        TODO()
    }
}

class LocalDateJs : TemporalJs {
    fun get(field: TemporalField): Int {
        TODO()
    }

    fun getYear(): Int {
        TODO()
    }

    fun getMonthValue(): Int {
        TODO()
    }

    fun getDayOfMonth(): Int {
        TODO()
    }

    fun withDayOfYear(dayOfYear: Int): LocalDate {
        TODO()
    }

    fun withDayOfMonth(dayOfMonth: Int): LocalDate {
        TODO()
    }

    fun with(field: TemporalField, newValue: Long): LocalDate {
        TODO()
    }

    fun range(field: TemporalField): ValueRange {
        TODO()
    }

    fun plus(amountToAdd: Long, field: TemporalUnit): LocalDate {
        TODO()
    }

    fun plusYears(years: Long): LocalDate {
        TODO()
    }

    fun minus(amountToSubtract: Long, field: TemporalUnit): LocalDate {
        TODO()
    }

    fun atStartOfDay(zone: ZoneId): ZonedDateTime {
        TODO()
    }
}

fun localDateOfJs(year: Int, month: Int, dayOfMonth: Int): LocalDate {
    TODO()
}

fun localDateParseJs(text: String): LocalDate {
    TODO()
}

class ZoneOffsetJs(private val offset: UtcOffset) {
    fun getTotalSeconds(): Int {
        TODO()
    }

    fun get(field: TemporalField): Int {
        TODO()
    }

    fun getId(): String {
        TODO()
    }
}

fun zoneOffsetOfHoursMinutesJs(hours: Int, minutes: Int): ZoneOffset {
    TODO()
}

abstract class TimeZoneJs {
    fun toZoneId(): ZoneId {
        TODO()
    }
}

fun timeZoneGetDefaultJs(): TimeZone {
    TODO()
}

abstract class CalendarJs {
    fun setTime(date: Date) {
        TODO()
    }

    fun toInstant(): Instant {
        TODO()
    }

    fun getTimeZone(): TimeZone {
        TODO()
    }
}

fun calendarGetInstanceJs(): Calendar {
    TODO()
}

class OffsetDateTimeJs(
    private val instant: kotlinx.datetime.Instant,
    private val offset: UtcOffset,
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
            instant.plus(amountToAdd, DateTimeUnit.YEAR, offset.asTimeZone()),
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

    fun with(field: TemporalField, newValue: Long): OffsetDateTime {
        TODO()
    }

    fun withDayOfYear(dayOfYear: Int): OffsetDateTime {
        TODO()
    }

    fun withDayOfMonth(dayOfMonth: Int): OffsetDateTime {
        TODO()
    }

    fun withOffsetSameInstant(offset: ZoneOffset): OffsetDateTime {
        TODO()
    }

    fun truncatedTo(unit: TemporalUnit): OffsetDateTime {
        TODO()
    }

    fun toInstant(): InstantJs {
        return InstantJs(instant)
    }

    fun range(field: TemporalField): ValueRange {
        TODO()
    }

    fun toLocalDate(): LocalDate {
        TODO()
    }

    fun toLocalDateTime(): LocalDateTime {
        TODO()
    }
}

class LocalDateTimeJs : TemporalJs

class InstantJs(private val instant: kotlinx.datetime.Instant)

fun offsetDateTimeParseJs(text: String): OffsetDateTime {
    TODO()
}

fun offsetDateTimeOfInstantJs(instant: Instant, offset: ZoneId): OffsetDateTimeJs {
    TODO()
}

fun dateTimeFormatterIsoOffsetDateTimeFormatJs(dateTime: OffsetDateTime): String {
    TODO()
}

public abstract class ZoneIdJs(private val zone: kotlinx.datetime.TimeZone)

class ZonedDateTimeJs(
    private val instant: kotlinx.datetime.Instant,
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

class LocalTimeJs : TemporalJs {
    fun getHour(): Int {
        TODO()
    }

    fun getMinute(): Int {
        TODO()
    }

    fun getSecond(): Int {
        TODO()
    }

    fun get(field: TemporalField): Int {
        TODO()
    }

    fun plusHours(hours: Long): LocalTime {
        TODO()
    }

    fun with(field: TemporalField, newValue: Long): LocalTime {
        TODO()
    }

    fun range(field: TemporalField): ValueRange {
        TODO()
    }

    fun truncatedTo(unit: TemporalUnit): LocalTime {
        TODO()
    }

    fun plus(amountToAdd: Long, unit: TemporalUnit): LocalTime {
        TODO()
    }

    fun minus(amountToSubtract: Long, unit: TemporalUnit): LocalTime {
        TODO()
    }
}

fun localTimeParseJs(s: String): LocalTime {
    TODO()
}
