package org.opencds.cqf.cql.engine.util

expect class Date {
    fun after(that: Date): Boolean
}

expect fun dateFrom(instant: Instant): Date

expect interface TemporalField

expect interface TemporalUnit

expect enum class ChronoField : TemporalField {
    YEAR,
    MONTH_OF_YEAR,
    DAY_OF_MONTH,
    HOUR_OF_DAY,
    MINUTE_OF_HOUR,
    SECOND_OF_MINUTE,
    MILLI_OF_SECOND,
    OFFSET_SECONDS,
}

expect interface Temporal

expect enum class ChronoUnit : TemporalUnit {
    YEARS,
    MONTHS,
    DAYS,
    HOURS,
    MINUTES,
    SECONDS,
    MILLIS;

    fun between(start: Temporal, end: Temporal): Long
}

expect class ValueRange {
    fun getMinimum(): Long

    fun getMaximum(): Long
}

expect class LocalDate : Temporal {
    fun get(field: TemporalField): Int

    fun getYear(): Int

    fun getMonthValue(): Int

    fun getDayOfMonth(): Int

    fun withDayOfYear(dayOfYear: Int): LocalDate

    fun withDayOfMonth(dayOfMonth: Int): LocalDate

    fun with(field: TemporalField, newValue: Long): LocalDate

    fun range(field: TemporalField): ValueRange

    fun plus(amountToAdd: Long, field: TemporalUnit): LocalDate

    fun plusYears(years: Long): LocalDate

    fun minus(amountToSubtract: Long, field: TemporalUnit): LocalDate

    fun atStartOfDay(zone: ZoneId): ZonedDateTime
}

expect fun localDateOf(year: Int, month: Int, dayOfMonth: Int): LocalDate

expect fun localDateParse(text: String): LocalDate

expect class ZoneOffset {
    fun getTotalSeconds(): Int

    fun get(field: TemporalField): Int

    fun getId(): String
}

expect fun zoneOffsetOfHoursMinutes(hours: Int, minutes: Int): ZoneOffset

expect abstract class TimeZone {
    fun toZoneId(): ZoneId
}

expect fun timeZoneGetDefault(): TimeZone

expect abstract class Calendar {
    fun setTime(date: Date)

    fun toInstant(): Instant

    fun getTimeZone(): TimeZone
}

expect fun calendarGetInstance(): Calendar

expect class OffsetDateTime : Temporal {
    fun getYear(): Int

    fun getMonthValue(): Int

    fun getDayOfMonth(): Int

    fun getHour(): Int

    fun getMinute(): Int

    fun getSecond(): Int

    fun getOffset(): ZoneOffset

    fun get(field: TemporalField): Int

    fun plus(amountToAdd: Long, unit: TemporalUnit): OffsetDateTime

    fun plusYears(years: Long): OffsetDateTime

    fun minus(amountToSubtract: Long, unit: TemporalUnit): OffsetDateTime

    fun with(field: TemporalField, newValue: Long): OffsetDateTime

    fun withDayOfYear(dayOfYear: Int): OffsetDateTime

    fun withDayOfMonth(dayOfMonth: Int): OffsetDateTime

    fun withOffsetSameInstant(offset: ZoneOffset): OffsetDateTime

    fun truncatedTo(unit: TemporalUnit): OffsetDateTime

    fun toInstant(): Instant

    fun range(field: TemporalField): ValueRange

    fun toLocalDate(): LocalDate

    fun toLocalDateTime(): LocalDateTime
}

expect class LocalDateTime : Temporal

expect class Instant

expect fun offsetDateTimeParse(text: String): OffsetDateTime

expect fun offsetDateTimeOfInstant(instant: Instant, offset: ZoneId): OffsetDateTime

expect fun dateTimeFormatterIsoOffsetDateTimeFormat(dateTime: OffsetDateTime): String

expect abstract class ZoneId

expect class ZonedDateTime {
    fun getZone(): ZoneId

    fun getOffset(): ZoneOffset

    fun toInstant(): Instant

    fun toOffsetDateTime(): OffsetDateTime
}

expect fun zonedDateTimeNow(): ZonedDateTime

expect class LocalTime : Temporal {
    fun getHour(): Int

    fun getMinute(): Int

    fun getSecond(): Int

    fun get(field: TemporalField): Int

    fun plusHours(hours: Long): LocalTime

    fun with(field: TemporalField, newValue: Long): LocalTime

    fun range(field: TemporalField): ValueRange

    fun truncatedTo(unit: TemporalUnit): LocalTime

    fun plus(amountToAdd: Long, unit: TemporalUnit): LocalTime

    fun minus(amountToSubtract: Long, unit: TemporalUnit): LocalTime
}

expect fun localTimeParse(s: String): LocalTime
