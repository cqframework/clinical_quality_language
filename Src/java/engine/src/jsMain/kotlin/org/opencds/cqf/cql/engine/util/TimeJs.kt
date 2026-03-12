package org.opencds.cqf.cql.engine.util

// https://github.com/Kotlin/kotlinx-datetime#note-about-time-zones-in-js
@JsModule("@js-joda/timezone") @JsNonModule external object JsJodaTimeZoneModule

@OptIn(ExperimentalJsExport::class) @JsExport val jsJodaTz = JsJodaTimeZoneModule

actual typealias Date = DateJs

actual fun dateFrom(instant: Instant): Date {
    return dateFromJs(instant)
}

actual typealias TemporalField = TemporalFieldJs

actual typealias TemporalUnit = TemporalUnitJs

actual typealias ChronoField = ChronoFieldJs

actual typealias Temporal = TemporalJs

actual typealias ChronoUnit = ChronoUnitJs

actual typealias ValueRange = ValueRangeJs

actual typealias LocalDate = LocalDateJs

actual fun localDateOf(year: Int, month: Int, dayOfMonth: Int): LocalDate {
    return localDateOfJs(year, month, dayOfMonth)
}

actual fun localDateParse(text: String): LocalDate {
    return localDateParseJs(text)
}

actual typealias ZoneOffset = ZoneOffsetJs

actual fun zoneOffsetOfHoursMinutes(hours: Int, minutes: Int): ZoneOffset {
    return zoneOffsetOfHoursMinutesJs(hours, minutes)
}

actual typealias TimeZone = TimeZoneJs

actual fun timeZoneGetDefault(): TimeZone {
    return timeZoneGetDefaultJs()
}

actual typealias Calendar = CalendarJs

actual fun calendarGetInstance(): Calendar {
    return calendarGetInstanceJs()
}

actual typealias OffsetDateTime = OffsetDateTimeJs

actual fun offsetDateTimeOf(localDateTime: LocalDateTime, zoneOffset: ZoneOffset): OffsetDateTime {
    return offsetDateTimeOfJs(localDateTime, zoneOffset)
}

actual typealias LocalDateTime = LocalDateTimeJs

actual fun localDateTimeOf(
    year: Int,
    month: Int,
    dayOfMonth: Int,
    hour: Int,
    minute: Int,
    second: Int,
): LocalDateTime {
    return localDateTimeOfJs(year, month, dayOfMonth, hour, minute, second)
}

actual typealias Instant = InstantJs

actual fun offsetDateTimeParse(text: String): OffsetDateTime {
    return offsetDateTimeParseJs(text)
}

actual fun offsetDateTimeOfInstant(instant: Instant, offset: ZoneId): OffsetDateTime {
    return offsetDateTimeOfInstantJs(instant, offset)
}

actual fun dateTimeFormatterIsoOffsetDateTimeFormat(dateTime: OffsetDateTime): String {
    return dateTimeFormatterIsoOffsetDateTimeFormatJs(dateTime)
}

actual typealias ZoneId = ZoneIdJs

actual fun zoneIdOf(zoneId: String): ZoneId {
    return zoneIdOfJs(zoneId)
}

actual fun zoneIdToZoneOffset(zoneId: ZoneId, localDateTime: LocalDateTime): ZoneOffset {
    return zoneIdToZoneOffsetJs(zoneId, localDateTime)
}

actual typealias ZonedDateTime = ZonedDateTimeJs

actual fun zonedDateTimeNow(): ZonedDateTime {
    return zonedDateTimeNowJs()
}

actual typealias LocalTime = LocalTimeJs

actual fun localTimeParse(s: String): LocalTime {
    return localTimeParseJs(s)
}
