package org.opencds.cqf.cql.engine.util

import java.time.format.DateTimeFormatter

actual typealias Date = java.util.Date

actual fun dateFrom(instant: Instant): Date {
    return Date.from(instant)
}

actual typealias TemporalField = java.time.temporal.TemporalField

actual typealias TemporalUnit = java.time.temporal.TemporalUnit

actual typealias ChronoField = java.time.temporal.ChronoField

actual typealias Temporal = java.time.temporal.Temporal

actual typealias ChronoUnit = java.time.temporal.ChronoUnit

actual typealias ValueRange = java.time.temporal.ValueRange

actual typealias LocalDate = java.time.LocalDate

actual fun localDateOf(year: Int, month: Int, dayOfMonth: Int): LocalDate {
    return LocalDate.of(year, month, dayOfMonth)
}

actual fun localDateParse(text: String): LocalDate {
    return LocalDate.parse(text)
}

actual typealias ZoneOffset = java.time.ZoneOffset

actual fun zoneOffsetOfHoursMinutes(hours: Int, minutes: Int): ZoneOffset {
    return ZoneOffset.ofHoursMinutes(hours, minutes)
}

actual typealias TimeZone = java.util.TimeZone

actual fun timeZoneGetDefault(): TimeZone {
    return TimeZone.getDefault()
}

actual typealias Calendar = java.util.Calendar

actual fun calendarGetInstance(): Calendar {
    return Calendar.getInstance()
}

actual typealias OffsetDateTime = java.time.OffsetDateTime

actual typealias LocalDateTime = java.time.LocalDateTime

actual typealias Instant = java.time.Instant

actual fun offsetDateTimeParse(text: String): OffsetDateTime {
    return OffsetDateTime.parse(text)
}

actual fun offsetDateTimeOfInstant(instant: Instant, offset: ZoneId): OffsetDateTime {
    return OffsetDateTime.ofInstant(instant, offset)
}

actual fun dateTimeFormatterIsoOffsetDateTimeFormat(dateTime: OffsetDateTime): String {
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime)
}

actual typealias ZoneId = java.time.ZoneId

actual typealias ZonedDateTime = java.time.ZonedDateTime

actual fun zonedDateTimeNow(): ZonedDateTime {
    return ZonedDateTime.now()
}

actual typealias LocalTime = java.time.LocalTime

actual fun localTimeParse(s: String): LocalTime {
    return LocalTime.parse(s)
}
