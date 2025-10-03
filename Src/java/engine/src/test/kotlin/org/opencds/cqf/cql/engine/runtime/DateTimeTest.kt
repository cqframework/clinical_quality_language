package org.opencds.cqf.cql.engine.runtime

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.exception.CqlException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class DateTimeTest {
    @ParameterizedTest
    @MethodSource("dateStrings")
    fun dateStringsTest(dateString: String, zoneOffset: ZoneOffset, precision: Precision) {
        val dateTime = DateTime(dateString, zoneOffset)

        val normalizedDateTime = dateTime.getNormalized(precision)

        Assertions.assertEquals(normalizedDateTime, dateTime.dateTime)
    }

    @Test
    fun dateStringsNullOffsets() {
        Assertions.assertThrows(CqlException::class.java) {
            DateTime(DST_2023_10_26_22_12_0_STRING, null)
        }
    }

    @ParameterizedTest
    @MethodSource("dateStringsOtherZoneId")
    fun dateStringsOtherZoneIdTest(
        localDateTime: LocalDateTime,
        zoneOffsetInit: ZoneOffset,
        zonedOffsetGetNormalized: ZoneOffset,
        precision: Precision,
    ) {
        val expectedOffsetDateTime =
            OffsetDateTime.of(
                localDateTime.minusSeconds(
                    (zoneOffsetInit.totalSeconds - zonedOffsetGetNormalized.totalSeconds).toLong()
                ),
                zonedOffsetGetNormalized,
            )
        val dateTime = DateTime(FORMATTER.format(localDateTime), zoneOffsetInit)

        val normalizedDateTime = dateTime.getNormalized(precision, zonedOffsetGetNormalized)

        Assertions.assertEquals(normalizedDateTime, expectedOffsetDateTime)
    }

    @ParameterizedTest
    @MethodSource("offsetPrecisions")
    fun offsetPrecisionsTest(
        localDateTime: LocalDateTime,
        zoneOffset: ZoneOffset,
        precision: Precision,
    ) {
        val offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset)
        val dateTimeNoPrecision = DateTime(offsetDateTime)
        val dateTimePrecision = DateTime(offsetDateTime, precision)

        val normalizedDateTimeNoPrecision = dateTimeNoPrecision.getNormalized(precision)
        val normalizedDateTimePrecision = dateTimePrecision.getNormalized(precision)

        Assertions.assertEquals(normalizedDateTimeNoPrecision, dateTimeNoPrecision.dateTime)
        Assertions.assertEquals(normalizedDateTimePrecision, dateTimePrecision.dateTime)
    }

    @ParameterizedTest
    @MethodSource("bigDecimals")
    fun bigDecimal(offset: BigDecimal, precision: Precision, dateElements: MutableList<Int?>) {
        val dateElementsArray = dateElements.stream().mapToInt { anInt -> anInt!! }.toArray()
        val dateTime = DateTime(offset, *dateElementsArray)

        val normalizedDateTime = dateTime.getNormalized(precision)

        logger.warn(
            "TEST: {}, offset: {}, precision: {}, dateElements: {}, actualDateTime: {}, expectedDateTime: {}",
            dateTime.dateTime == normalizedDateTime,
            offset,
            precision,
            dateElements,
            normalizedDateTime,
            dateTime.dateTime,
        )

        Assertions.assertEquals(normalizedDateTime, dateTime.dateTime)
    }

    @Test
    fun nullBigDecimalOffset() {
        val digits: IntArray =
            DST_2023_10_26_22_12_0_INTS.stream().mapToInt { anInt -> anInt!! }.toArray()
        Assertions.assertThrows(CqlException::class.java) { DateTime(null, *digits) }
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    fun bigDecimalWithCustomTimezoneAndNow(zoneId: ZoneId, now: LocalDateTime?) {
        val currentOffsetForMyZone = zoneId.rules.getOffset(now)
        val offset = TemporalHelper.zoneToOffset(currentOffsetForMyZone)

        val dateElementsArray: IntArray =
            DST_2023_10_26_22_12_0_INTS.stream().mapToInt { anInt -> anInt!! }.toArray()
        val dateTime = DateTime(offset, *dateElementsArray)

        val normalizedDateTime = dateTime.getNormalized(Precision.HOUR)

        Assertions.assertEquals(normalizedDateTime, dateTime.dateTime)
    }

    @Test
    fun dateTimeEquals() {
        val dateTime = DateTime(BigDecimal.ONE, 2020)

        Assertions.assertEquals(dateTime, dateTime)
        Assertions.assertNotEquals(null, dateTime)
        Assertions.assertNotEquals(1, dateTime)

        var dateTime2 = DateTime(BigDecimal.ONE, 2020)

        // identical constructors
        Assertions.assertEquals(dateTime, dateTime2)

        // varying offset
        dateTime2 = DateTime(BigDecimal.valueOf(2L), 2020)
        Assertions.assertNotEquals(dateTime, dateTime2)

        // varying year
        dateTime2 = DateTime(BigDecimal.ONE, 2021)
        Assertions.assertNotEquals(dateTime, dateTime2)

        // varying precision
        dateTime2 = DateTime(BigDecimal.ONE, 2020, 3)
        Assertions.assertNotEquals(dateTime, dateTime2)

        // TODO: It'd be good to extend these across different types of constructors, for example,
        // from a string
    }

    @Test
    fun roundToHigherPrecision() {
        val offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45+11:00")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.MILLISECOND, false) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MILLISECOND, true) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.SECOND, false) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.SECOND, true) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+11:00", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)
    }

    @Test
    fun roundToSamePrecision() {
        val offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45+12:45")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.MINUTE, false) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+12:45", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MINUTE, true) as DateTime
        Assertions.assertEquals("2025-07-15T10:30+12:45", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MINUTE, roundedDateTime.precision)
    }

    @Test
    fun roundToLowerPrecision() {
        val offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45-04:30")
        val dateTime = DateTime(offsetDateTime, Precision.MINUTE)

        var roundedDateTime = dateTime.roundToPrecision(Precision.HOUR, false) as DateTime
        Assertions.assertEquals("2025-07-15T10:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.HOUR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.HOUR, true) as DateTime
        Assertions.assertEquals("2025-07-15T11:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.HOUR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.DAY, false) as DateTime
        Assertions.assertEquals("2025-07-15T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.DAY, true) as DateTime
        Assertions.assertEquals("2025-07-16T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.WEEK, false) as DateTime
        Assertions.assertEquals("2025-07-15T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.WEEK, true) as DateTime
        Assertions.assertEquals("2025-07-16T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.DAY, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MONTH, false) as DateTime
        Assertions.assertEquals("2025-07-01T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MONTH, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.MONTH, true) as DateTime
        Assertions.assertEquals("2025-08-01T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.MONTH, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.YEAR, false) as DateTime
        Assertions.assertEquals("2025-01-01T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.YEAR, roundedDateTime.precision)

        roundedDateTime = dateTime.roundToPrecision(Precision.YEAR, true) as DateTime
        Assertions.assertEquals("2026-01-01T00:00-04:30", roundedDateTime.dateTime.toString())
        Assertions.assertEquals(Precision.YEAR, roundedDateTime.precision)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DateTimeTest::class.java)

        private val DST_2023_10_26_22_12_0: LocalDateTime =
            LocalDateTime.of(2023, Month.OCTOBER, 26, 22, 12, 0)
        private val DST_2023_11_03_02_52_0: LocalDateTime =
            LocalDateTime.of(2023, Month.NOVEMBER, 3, 2, 52, 0)

        // This is OUTSIDE of Daylight Savings Time
        private val NON_DST_2024_02_27_07_28_0: LocalDateTime =
            LocalDateTime.of(2024, Month.FEBRUARY, 27, 7, 28, 0)
        private val DST_2024_06_15_23_32_0: LocalDateTime =
            LocalDateTime.of(2024, Month.JULY, 15, 23, 32, 0)

        private val FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        private val DST_2023_10_26_22_12_0_STRING: String = FORMATTER.format(DST_2023_10_26_22_12_0)
        private val DST_2023_11_03_02_52_0_STRING: String = FORMATTER.format(DST_2023_11_03_02_52_0)
        private val NON_DST_2024_02_27_07_28_0_STRING: String =
            FORMATTER.format(NON_DST_2024_02_27_07_28_0)
        private val DST_2024_06_15_23_32_0_STRING: String = FORMATTER.format(DST_2024_06_15_23_32_0)

        private val DST_2023_10_26_22_12_0_INTS: List<Int> = toList(DST_2024_06_15_23_32_0)

        private val DST_2023_11_03_02_52_0_INTS: List<Int> = toList(DST_2023_11_03_02_52_0)
        private val NON_DST_2024_02_27_07_28_0_INTS: List<Int> = toList(NON_DST_2024_02_27_07_28_0)
        private val DST_2024_06_15_23_32_0_INTS: List<Int> = toList(DST_2024_06_15_23_32_0)

        private val DST_OFFSET_NORTH_AMERICA_EASTERN: ZoneOffset = ZoneOffset.of("-04:30")
        private val NON_DST_OFFSET_NORTH_AMERICA_EASTERN: ZoneOffset = ZoneOffset.of("-05:00")
        private val DST_OFFSET_NORTH_AMERICA_MOUNTAIN: ZoneOffset = ZoneOffset.of("-06:00")
        private val NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN: ZoneOffset = ZoneOffset.of("-07:00")
        private val DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND: ZoneOffset = ZoneOffset.of("-02:30")

        // This offset doesn't exist for an ZoneID
        private val NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND: ZoneOffset = ZoneOffset.of("-03:30")

        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN: BigDecimal =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_EASTERN)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN: BigDecimal =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_EASTERN)
        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN: BigDecimal =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_MOUNTAIN)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN: BigDecimal =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN)
        private val DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND: BigDecimal =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND)
        private val NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND: BigDecimal =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND)

        private fun toBigDecimal(zoneOffset: ZoneOffset): BigDecimal {
            val offsetSeconds = zoneOffset.getLong(ChronoField.OFFSET_SECONDS)
            val offsetMinutes =
                BigDecimal.valueOf(offsetSeconds)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING)
            return offsetMinutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING)
        }

        private fun toList(localDateTime: LocalDateTime): List<Int> {
            return listOf(
                localDateTime.year,
                localDateTime.monthValue,
                localDateTime.dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                localDateTime.second,
            )
        }

        @JvmStatic
        private fun dateStrings(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(DST_2023_10_26_22_12_0_STRING, ZoneOffset.UTC, Precision.HOUR),
                arrayOf(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(DST_2023_11_03_02_52_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND),
                arrayOf(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_11_03_02_52_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                arrayOf(NON_DST_2024_02_27_07_28_0_STRING, ZoneOffset.UTC, Precision.HOUR),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0_STRING,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(DST_2024_06_15_23_32_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND),
                arrayOf(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2024_06_15_23_32_0_STRING,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
            )
        }

        @JvmStatic
        private fun dateStringsOtherZoneId(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
            )
        }

        @JvmStatic
        private fun offsetPrecisions(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(DST_2023_10_26_22_12_0, ZoneOffset.UTC, Precision.HOUR),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR),
                arrayOf(
                    DST_2023_10_26_22_12_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(DST_2023_11_03_02_52_0, ZoneOffset.UTC, Precision.MILLISECOND),
                arrayOf(
                    DST_2023_11_03_02_52_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_11_03_02_52_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2023_11_03_02_52_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
                arrayOf(NON_DST_2024_02_27_07_28_0, ZoneOffset.UTC, Precision.HOUR),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                ),
                arrayOf(
                    NON_DST_2024_02_27_07_28_0,
                    NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                ),
                arrayOf(DST_2024_06_15_23_32_0, ZoneOffset.UTC, Precision.MILLISECOND),
                arrayOf(
                    DST_2024_06_15_23_32_0,
                    NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2024_06_15_23_32_0,
                    DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                ),
                arrayOf(
                    DST_2024_06_15_23_32_0,
                    DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                ),
            )
        }

        @JvmStatic
        private fun bigDecimals(): Array<Array<Any?>?> {
            return arrayOf(
                arrayOf(BigDecimal.ZERO, Precision.HOUR, DST_2023_10_26_22_12_0_INTS),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                    DST_2023_10_26_22_12_0_INTS,
                ),
                arrayOf(BigDecimal.ZERO, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                    DST_2023_11_03_02_52_0_INTS,
                ),
                arrayOf(BigDecimal.ZERO, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS),
                arrayOf(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                arrayOf(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                arrayOf(
                    NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.HOUR,
                    NON_DST_2024_02_27_07_28_0_INTS,
                ),
                arrayOf(BigDecimal.ZERO, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN,
                    Precision.MILLISECOND,
                    DST_2024_06_15_23_32_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN,
                    Precision.HOUR,
                    DST_2024_06_15_23_32_0_INTS,
                ),
                arrayOf(
                    DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                    Precision.MILLISECOND,
                    DST_2024_06_15_23_32_0_INTS,
                ),
            )
        }

        private val UTC: ZoneId = ZoneId.of("UTC")
        private val MONTREAL: ZoneId = ZoneId.of("America/Montreal")
        private val REGINA: ZoneId =
            ZoneId.of(
                "America/Regina"
            ) // Saskatchewan does not have standard time (non-DST) all year round
        private val DST_2023_11_01: LocalDateTime =
            LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0)
        private val NON_DST_2023_11_13: LocalDateTime =
            LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0)

        @JvmStatic
        private fun timeZones(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(UTC, DST_2023_11_01),
                arrayOf(MONTREAL, DST_2023_11_01),
                arrayOf(REGINA, DST_2023_11_01),
                arrayOf(UTC, NON_DST_2023_11_13),
                arrayOf(MONTREAL, NON_DST_2023_11_13),
                arrayOf(REGINA, NON_DST_2023_11_13),
            )
        }
    }
}
