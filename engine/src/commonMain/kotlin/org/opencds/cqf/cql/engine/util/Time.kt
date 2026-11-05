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

expect class ZoneOffset : ZoneId {
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

expect fun offsetDateTimeOf(localDateTime: LocalDateTime, zoneOffset: ZoneOffset): OffsetDateTime

expect class LocalDateTime : Temporal {
    fun getYear(): Int

    fun getMonthValue(): Int

    fun getDayOfMonth(): Int

    fun getHour(): Int

    fun getMinute(): Int

    fun getSecond(): Int

    fun minusSeconds(seconds: Long): LocalDateTime
}

expect fun localDateTimeOf(
    year: Int,
    month: Int,
    dayOfMonth: Int,
    hour: Int,
    minute: Int,
    second: Int,
): LocalDateTime

expect class Instant

expect fun offsetDateTimeParse(text: String): OffsetDateTime

expect fun offsetDateTimeOfInstant(instant: Instant, offset: ZoneId): OffsetDateTime

expect fun dateTimeFormatterIsoOffsetDateTimeFormat(dateTime: OffsetDateTime): String

expect abstract class ZoneId

expect fun zoneIdOf(zoneId: String): ZoneId

expect fun zoneIdToZoneOffset(zoneId: ZoneId, localDateTime: LocalDateTime): ZoneOffset

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

/** Returns true if the year has 366 days. */
internal fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

/** Returns the number of days in the given month. */
internal fun monthLengthInDays(year: Int, month: Int): Int {
    return when (month) {
        2 -> if (isLeapYear(year)) 29 else 28
        4,
        6,
        9,
        11 -> 30
        else -> 31
    }
}

/** Returns true if the date falls on the last day of February. */
fun LocalDate.isLastDayOfFeb(): Boolean {
    if (this.getMonthValue() == 2) {
        val monthLength = monthLengthInDays(this.getYear(), this.getMonthValue())
        return this.getDayOfMonth() == monthLength
    }
    return false
}

/** Returns true if the *local* date time falls on the last day of February. */
fun OffsetDateTime.isLastDayOfFeb(): Boolean {
    if (this.getMonthValue() == 2) {
        val monthLength = monthLengthInDays(this.getYear(), this.getMonthValue())
        return this.getDayOfMonth() == monthLength
    }
    return false
}
