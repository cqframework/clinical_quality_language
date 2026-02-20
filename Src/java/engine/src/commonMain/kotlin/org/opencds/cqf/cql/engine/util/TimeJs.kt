package org.opencds.cqf.cql.engine.util

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

class ZoneOffsetJs {
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

class OffsetDateTimeJs : TemporalJs {
    fun getYear(): Int {
        TODO()
    }

    fun getMonthValue(): Int {
        TODO()
    }

    fun getDayOfMonth(): Int {
        TODO()
    }

    fun getHour(): Int {
        TODO()
    }

    fun getMinute(): Int {
        TODO()
    }

    fun getSecond(): Int {
        TODO()
    }

    fun getOffset(): ZoneOffset {
        TODO()
    }

    fun get(field: TemporalField): Int {
        TODO()
    }

    fun plus(amountToAdd: Long, unit: TemporalUnit): OffsetDateTime {
        TODO()
    }

    fun plusYears(years: Long): OffsetDateTime {
        TODO()
    }

    fun minus(amountToSubtract: Long, unit: TemporalUnit): OffsetDateTime {
        TODO()
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

    fun toInstant(): Instant {
        TODO()
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

class InstantJs

fun offsetDateTimeParseJs(text: String): OffsetDateTime {
    TODO()
}

fun offsetDateTimeOfInstantJs(instant: Instant, offset: ZoneId): OffsetDateTime {
    TODO()
}

fun dateTimeFormatterIsoOffsetDateTimeFormatJs(dateTime: OffsetDateTime): String {
    TODO()
}

public abstract class ZoneIdJs

class ZonedDateTimeJs {
    fun getZone(): ZoneId {
        TODO()
    }

    fun getOffset(): ZoneOffset {
        TODO()
    }

    fun toInstant(): Instant {
        TODO()
    }

    fun toOffsetDateTime(): OffsetDateTime {
        TODO()
    }
}

fun zonedDateTimeNowJs(): ZonedDateTime {
    TODO()
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
