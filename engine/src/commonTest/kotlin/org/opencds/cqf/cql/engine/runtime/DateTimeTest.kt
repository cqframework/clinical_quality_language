package org.opencds.cqf.cql.engine.runtime

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.ONE
import org.cqframework.cql.shared.RoundingMode
import org.cqframework.cql.shared.ZERO
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.util.ChronoField
import org.opencds.cqf.cql.engine.util.LocalDateTime
import org.opencds.cqf.cql.engine.util.ZoneOffset
import org.opencds.cqf.cql.engine.util.localDateTimeOf
import org.opencds.cqf.cql.engine.util.offsetDateTimeOf
import org.opencds.cqf.cql.engine.util.offsetDateTimeParse
import org.opencds.cqf.cql.engine.util.toPaddedString
import org.opencds.cqf.cql.engine.util.zoneIdOf
import org.opencds.cqf.cql.engine.util.zoneIdToZoneOffset
import org.opencds.cqf.cql.engine.util.zoneOffsetOfHoursMinutes

internal class DateTimeTest {

    @Test
    fun dateStringsTest() {
        data class TestParameters(
            val dateString: kotlin.String,
            val zoneOffset: ZoneOffset,
            val precision: Precision,
        )

        for ((dateString, zoneOffset, precision) in
            arrayOf(
                TestParameters(DST_2023_10_26_22_12_0_STRING, zoneOffsetUtc, Precision.HOUR),
                TestParameters(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(DST_2023_11_03_02_52_0_STRING, zoneOffsetUtc, Precision.MILLISECOND),
                TestParameters(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                TestParameters(NON_DST_2024_02_27_07_28_0_STRING, zoneOffsetUtc, Precision.HOUR),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(DST_2024_06_15_23_32_0_STRING, zoneOffsetUtc, Precision.MILLISECOND),
                TestParameters(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
            )) {

            val dateTime = DateTime(dateString, zoneOffset)

            val normalizedDateTime = dateTime.getNormalized(precision)

            assertEquals(normalizedDateTime, dateTime.dateTime)
        }
    }

    @Test
    fun dateStringsNullOffsets() {

        assertFailsWith(CqlException::class) { DateTime(DST_2023_10_26_22_12_0_STRING, null) }
    }

    @Test
    fun dateStringsOtherZoneIdTest() {
        data class TestParameters(
            val localDateTime: LocalDateTime,
            val zoneOffsetInit: ZoneOffset,
            val zonedOffsetGetNormalized: ZoneOffset,
            val precision: Precision,
        )

        for ((
            localDateTime,
            zoneOffsetInit,
            zonedOffsetGetNormalized,
            precision,
        ) in
            arrayOf(
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
            )) {

            val expectedOffsetDateTime =
                offsetDateTimeOf(
                    localDateTime.minusSeconds(
                        (zoneOffsetInit.getTotalSeconds() -
                                zonedOffsetGetNormalized.getTotalSeconds())
                            .toLong()
                    ),
                    zonedOffsetGetNormalized,
                )
            val dateTime = DateTime(formatLocalDateTime(localDateTime), zoneOffsetInit)

            val normalizedDateTime = dateTime.getNormalized(precision, zonedOffsetGetNormalized)

            assertEquals(normalizedDateTime, expectedOffsetDateTime)
        }
    }

    @Test
    fun offsetPrecisionsTest() {
        data class TestParameters(
            val localDateTime: LocalDateTime,
            val zoneOffset: ZoneOffset,
            val precision: Precision,
        )

        for ((
            localDateTime,
            zoneOffset,
            precision,
        ) in
            arrayOf(
                TestParameters(DST_2023_10_26_22_12_0, zoneOffsetUtc, Precision.HOUR),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(DST_2023_11_03_02_52_0, zoneOffsetUtc, Precision.MILLISECOND),
                TestParameters(
                    DST_2023_11_03_02_52_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_11_03_02_52_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2023_11_03_02_52_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                TestParameters(NON_DST_2024_02_27_07_28_0, zoneOffsetUtc, Precision.HOUR),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                TestParameters(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                TestParameters(DST_2024_06_15_23_32_0, zoneOffsetUtc, Precision.MILLISECOND),
                TestParameters(
                    DST_2024_06_15_23_32_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2024_06_15_23_32_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                TestParameters(
                    DST_2024_06_15_23_32_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
            )) {

            val offsetDateTime = offsetDateTimeOf(localDateTime, zoneOffset)
            val dateTimeNoPrecision = DateTime(offsetDateTime)
            val dateTimePrecision = DateTime(offsetDateTime, precision)

            val normalizedDateTimeNoPrecision = dateTimeNoPrecision.getNormalized(precision)
            val normalizedDateTimePrecision = dateTimePrecision.getNormalized(precision)

            assertEquals(normalizedDateTimeNoPrecision, dateTimeNoPrecision.dateTime)
            assertEquals(normalizedDateTimePrecision, dateTimePrecision.dateTime)
        }
    }

    private data class BigDecimalParameters(
        val offset: BigDecimal,
        val precision: Precision,
        val dateElements: kotlin.collections.List<Int>,
    )

    @Test
    fun bigDecimal() {
        for ((
            offset,
            precision,
            dateElements,
        ) in
            arrayOf(
                BigDecimalParameters(ZERO, Precision.HOUR, DST_2023_10_26_22_12_0_INTS),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                BigDecimalParameters(ZERO, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                BigDecimalParameters(ZERO, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS),
                BigDecimalParameters(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                BigDecimalParameters(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                BigDecimalParameters(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                BigDecimalParameters(ZERO, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                    DST_2024_06_15_23_32_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    DST_2024_06_15_23_32_0_INTS,
                ),
                BigDecimalParameters(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                    DST_2024_06_15_23_32_0_INTS,
                ),
            )) {

            val dateElementsArray = dateElements.toIntArray()
            val dateTime = DateTime(offset, *dateElementsArray)

            val normalizedDateTime = dateTime.getNormalized(precision)

            logger.warn {
                "TEST: ${dateTime.dateTime == normalizedDateTime}, offset: $offset, precision: $precision, dateElements: $dateElements, actualDateTime: $normalizedDateTime, expectedDateTime: ${dateTime.dateTime}"
            }

            assertEquals(normalizedDateTime, dateTime.dateTime)
        }
    }

    @Test
    fun nullBigDecimalOffset() {
        val digits = DST_2023_10_26_22_12_0_INTS.toIntArray()
        assertFailsWith(CqlException::class) { DateTime(null, *digits) }
    }

    @Test
    fun bigDecimalWithCustomTimezoneAndNow() {
        for ((zoneId, now) in
            arrayOf(
                UTC to DST_2023_11_01,
                MONTREAL to DST_2023_11_01,
                REGINA to DST_2023_11_01,
                UTC to NON_DST_2023_11_13,
                MONTREAL to NON_DST_2023_11_13,
                REGINA to NON_DST_2023_11_13,
            )) {
            val currentOffsetForMyZone = zoneIdToZoneOffset(zoneId, now)
            val offset = TemporalHelper.zoneToOffset(currentOffsetForMyZone)

            val dateElementsArray = DST_2023_10_26_22_12_0_INTS.toIntArray()
            val dateTime = DateTime(offset, *dateElementsArray)

            val normalizedDateTime = dateTime.getNormalized(Precision.HOUR)

            assertEquals(normalizedDateTime, dateTime.dateTime)
        }
    }

    @Test
    fun dateTimeEquals() {
        val dateTime = DateTime(ONE, 2020)

        assertEquals(dateTime, dateTime)

        var dateTime2 = DateTime(ONE, 2020)

        // identical constructors
        assertEquals(dateTime, dateTime2)

        // varying offset
        dateTime2 = DateTime(BigDecimal(2L), 2020)
        assertNotEquals(dateTime, dateTime2)

        // varying year
        dateTime2 = DateTime(ONE, 2021)
        assertNotEquals(dateTime, dateTime2)

        // varying precision
        dateTime2 = DateTime(ONE, 2020, 3)
        assertNotEquals(dateTime, dateTime2)

        // TODO: It'd be good to extend these across different types of constructors, for example,
        // from a string
    }

    @Test
    fun roundToHigherPrecision() {
        val offsetDateTime = offsetDateTimeParse("2025-07-15T10:30:45+11:00")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.MILLISECOND, false) as DateTime
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MILLISECOND, true) as DateTime
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.SECOND, false) as DateTime
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.SECOND, true) as DateTime
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)
    }

    @Test
    fun roundToSamePrecision() {
        val offsetDateTime = offsetDateTimeParse("2025-07-15T10:30:45+12:45")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.MINUTE, false) as DateTime
        assertEquals("2025-07-15T10:30+12:45", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MINUTE, true) as DateTime
        assertEquals("2025-07-15T10:30+12:45", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MINUTE, roundedDateTime.precision)
    }

    @Test
    fun roundToLowerPrecision() {
        val offsetDateTime = offsetDateTimeParse("2025-07-15T10:30:45-04:30")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.HOUR, false) as DateTime
        assertEquals("2025-07-15T10:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.HOUR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.HOUR, true) as DateTime
        assertEquals("2025-07-15T11:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.HOUR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.DAY, false) as DateTime
        assertEquals("2025-07-15T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.DAY, true) as DateTime
        assertEquals("2025-07-16T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.WEEK, false) as DateTime
        assertEquals("2025-07-15T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.WEEK, true) as DateTime
        assertEquals("2025-07-16T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MONTH, false) as DateTime
        assertEquals("2025-07-01T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MONTH, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MONTH, true) as DateTime
        assertEquals("2025-08-01T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.MONTH, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.YEAR, false) as DateTime
        assertEquals("2025-01-01T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.YEAR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.YEAR, true) as DateTime
        assertEquals("2026-01-01T00:00-04:30", roundedDateTime.dateTime.toString())
        assertEquals(Precision.YEAR, roundedDateTime.precision)
    }

    companion object {
        private val logger = KotlinLogging.logger("DateTimeTest")

        private val zoneOffsetUtc = zoneOffsetOfHoursMinutes(0, 0)

        private val DST_2023_10_26_22_12_0 = localDateTimeOf(2023, 10, 26, 22, 12, 0)
        private val DST_2023_11_03_02_52_0 = localDateTimeOf(2023, 11, 3, 2, 52, 0)

        // This is OUTSIDE of Daylight Savings Time
        private val NON_DST_2024_02_27_07_28_0 = localDateTimeOf(2024, 2, 27, 7, 28, 0)
        private val DST_2024_06_15_23_32_0 = localDateTimeOf(2024, 7, 15, 23, 32, 0)

        private fun formatLocalDateTime(localDateTime: LocalDateTime): kotlin.String {
            return "${localDateTime.getYear().toPaddedString(4)}-${localDateTime.getMonthValue().toPaddedString(2)}-${localDateTime.getDayOfMonth().toPaddedString(2)}T${localDateTime.getHour().toPaddedString(2)}:${localDateTime.getMinute().toPaddedString(2)}:${localDateTime.getSecond().toPaddedString(2)}"
        }

        private val DST_2023_10_26_22_12_0_STRING = formatLocalDateTime(DST_2023_10_26_22_12_0)
        private val DST_2023_11_03_02_52_0_STRING = formatLocalDateTime(DST_2023_11_03_02_52_0)
        private val NON_DST_2024_02_27_07_28_0_STRING =
            formatLocalDateTime(NON_DST_2024_02_27_07_28_0)
        private val DST_2024_06_15_23_32_0_STRING = formatLocalDateTime(DST_2024_06_15_23_32_0)

        private val DST_2023_10_26_22_12_0_INTS = toList(DST_2024_06_15_23_32_0)

        private val DST_2023_11_03_02_52_0_INTS = toList(DST_2023_11_03_02_52_0)
        private val NON_DST_2024_02_27_07_28_0_INTS = toList(NON_DST_2024_02_27_07_28_0)
        private val DST_2024_06_15_23_32_0_INTS = toList(DST_2024_06_15_23_32_0)

        private val DST_OFFSET_NORTH_AMERICA_EASTERN = zoneOffsetOfHoursMinutes(-4, -30)
        private val NON_DST_OFFSET_NORTH_AMERICA_EASTERN = zoneOffsetOfHoursMinutes(-5, 0)
        private val DST_OFFSET_NORTH_AMERICA_MOUNTAIN = zoneOffsetOfHoursMinutes(-6, 0)
        private val NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN = zoneOffsetOfHoursMinutes(-7, 0)
        private val DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = zoneOffsetOfHoursMinutes(-2, -30)

        // This offset doesn't exist for an ZoneID
        private val NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = zoneOffsetOfHoursMinutes(-3, -30)

        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_EASTERN)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_EASTERN)
        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_MOUNTAIN)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN)
        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND)

        private fun toBigDecimal(zoneOffset: ZoneOffset): BigDecimal {
            val offsetSeconds = zoneOffset.get(ChronoField.OFFSET_SECONDS)
            val offsetMinutes =
                BigDecimal(offsetSeconds).divide(BigDecimal(60), 2, RoundingMode.CEILING)
            return offsetMinutes.divide(BigDecimal(60), 2, RoundingMode.CEILING)
        }

        private fun toList(localDateTime: LocalDateTime): kotlin.collections.List<Int> {
            return listOf(
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond(),
            )
        }

        private val UTC = zoneIdOf("UTC")
        private val MONTREAL = zoneIdOf("America/Montreal")
        private val REGINA =
            zoneIdOf(
                "America/Regina"
            ) // Saskatchewan does not have standard time (non-DST) all year round
        private val DST_2023_11_01 = localDateTimeOf(2023, 11, 1, 0, 0, 0)
        private val NON_DST_2023_11_13 = localDateTimeOf(2023, 11, 13, 0, 0, 0)
    }
}
