package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DateTimeTest {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeTest.class);

    private static final LocalDateTime DST_2023_10_26_22_12_0 = LocalDateTime.of(2023, Month.OCTOBER, 26, 22, 12, 0);
    private static final LocalDateTime DST_2023_11_03_02_52_0 = LocalDateTime.of(2023, Month.NOVEMBER, 3, 2, 52, 0);
    // This is OUTSIDE of Daylight Savings Time
    private static final LocalDateTime NON_DST_2024_02_27_07_28_0 =
            LocalDateTime.of(2024, Month.FEBRUARY, 27, 7, 28, 0);
    private static final LocalDateTime DST_2024_06_15_23_32_0 = LocalDateTime.of(2024, Month.JULY, 15, 23, 32, 0);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DST_2023_10_26_22_12_0_STRING = FORMATTER.format(DST_2023_10_26_22_12_0);
    private static final String DST_2023_11_03_02_52_0_STRING = FORMATTER.format(DST_2023_11_03_02_52_0);
    private static final String NON_DST_2024_02_27_07_28_0_STRING = FORMATTER.format(NON_DST_2024_02_27_07_28_0);
    private static final String DST_2024_06_15_23_32_0_STRING = FORMATTER.format(DST_2024_06_15_23_32_0);

    private static final List<Integer> DST_2023_10_26_22_12_0_INTS = toList(DST_2024_06_15_23_32_0);

    private static final List<Integer> DST_2023_11_03_02_52_0_INTS = toList(DST_2023_11_03_02_52_0);
    private static final List<Integer> NON_DST_2024_02_27_07_28_0_INTS = toList(NON_DST_2024_02_27_07_28_0);
    private static final List<Integer> DST_2024_06_15_23_32_0_INTS = toList(DST_2024_06_15_23_32_0);

    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_EASTERN = ZoneOffset.of("-04:30");
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_EASTERN = ZoneOffset.of("-05:00");
    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_MOUNTAIN = ZoneOffset.of("-06:00");
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN = ZoneOffset.of("-07:00");
    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = ZoneOffset.of("-02:30");
    // This offset doesn't exist for an ZoneID
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = ZoneOffset.of("-03:30");

    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_EASTERN);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_EASTERN);
    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_MOUNTAIN);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN);
    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND =
            toBigDecimal(DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND =
            toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND);

    private static BigDecimal toBigDecimal(ZoneOffset zoneOffset) {
        final long offsetSeconds = zoneOffset.getLong(ChronoField.OFFSET_SECONDS);
        final BigDecimal offsetMinutes =
                BigDecimal.valueOf(offsetSeconds).divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
        return offsetMinutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
    }

    private static List<Integer> toList(LocalDateTime localDateTime) {
        return List.of(
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond());
    }

    private static Object[][] dateStrings() {
        return new Object[][] {
            {DST_2023_10_26_22_12_0_STRING, ZoneOffset.UTC, Precision.HOUR},
            {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
            {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
            {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
            {DST_2023_11_03_02_52_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
            {NON_DST_2024_02_27_07_28_0_STRING, ZoneOffset.UTC, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
            {DST_2024_06_15_23_32_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND}
        };
    }

    @ParameterizedTest
    @MethodSource("dateStrings")
    void dateStrings(String dateString, ZoneOffset zoneOffset, Precision precision) {
        final DateTime dateTime = new DateTime(dateString, zoneOffset);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    @Test
    void dateStringsNullOffsets() {
        assertThrows(CqlException.class, () -> new DateTime(DST_2023_10_26_22_12_0_STRING, null));
        ;
    }

    private static Object[][] dateStringsOtherZoneId() {
        return new Object[][] {
            {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_EASTERN, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.HOUR
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_EASTERN,
                DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                Precision.HOUR
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                DST_OFFSET_NORTH_AMERICA_EASTERN,
                Precision.HOUR
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.HOUR
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.HOUR
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                Precision.HOUR
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                Precision.HOUR
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_EASTERN,
                DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.MILLISECOND
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.MILLISECOND
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_EASTERN,
                DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                Precision.MILLISECOND
            },
            {
                DST_2023_10_26_22_12_0,
                DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                DST_OFFSET_NORTH_AMERICA_EASTERN,
                Precision.MILLISECOND
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.MILLISECOND
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN,
                Precision.MILLISECOND
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                Precision.MILLISECOND
            },
            {
                NON_DST_2024_02_27_07_28_0,
                NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND,
                NON_DST_OFFSET_NORTH_AMERICA_EASTERN,
                Precision.MILLISECOND
            },
        };
    }

    @ParameterizedTest
    @MethodSource("dateStringsOtherZoneId")
    void dateStringsOtherZoneId(
            LocalDateTime localDateTime,
            ZoneOffset zoneOffsetInit,
            ZoneOffset zonedOffsetGetNormalized,
            Precision precision) {
        final OffsetDateTime expectedOffsetDateTime = OffsetDateTime.of(
                localDateTime.minusSeconds(
                        zoneOffsetInit.getTotalSeconds() - zonedOffsetGetNormalized.getTotalSeconds()),
                zonedOffsetGetNormalized);
        final DateTime dateTime = new DateTime(FORMATTER.format(localDateTime), zoneOffsetInit);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision, zonedOffsetGetNormalized);

        assertEquals(normalizedDateTime, expectedOffsetDateTime);
    }

    private static Object[][] offsetPrecisions() {
        return new Object[][] {
            {DST_2023_10_26_22_12_0, ZoneOffset.UTC, Precision.HOUR},
            {DST_2023_10_26_22_12_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
            {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
            {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
            {DST_2023_11_03_02_52_0, ZoneOffset.UTC, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
            {DST_2023_11_03_02_52_0, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
            {NON_DST_2024_02_27_07_28_0, ZoneOffset.UTC, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
            {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
            {DST_2024_06_15_23_32_0, ZoneOffset.UTC, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
            {DST_2024_06_15_23_32_0, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
        };
    }

    @ParameterizedTest
    @MethodSource("offsetPrecisions")
    void offsetPrecisions(LocalDateTime localDateTime, ZoneOffset zoneOffset, Precision precision) {
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);
        final DateTime dateTimeNoPrecision = new DateTime(offsetDateTime);
        final DateTime dateTimePrecision = new DateTime(offsetDateTime, precision);

        final OffsetDateTime normalizedDateTimeNoPrecision = dateTimeNoPrecision.getNormalized(precision);
        final OffsetDateTime normalizedDateTimePrecision = dateTimePrecision.getNormalized(precision);

        assertEquals(normalizedDateTimeNoPrecision, dateTimeNoPrecision.getDateTime());
        assertEquals(normalizedDateTimePrecision, dateTimePrecision.getDateTime());
    }

    private static Object[][] bigDecimals() {
        return new Object[][] {
            {BigDecimal.ZERO, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
            {BigDecimal.ZERO, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
            {BigDecimal.ZERO, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
            {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
            {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
            {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
            {BigDecimal.ZERO, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, DST_2024_06_15_23_32_0_INTS},
            {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
        };
    }

    @ParameterizedTest
    @MethodSource("bigDecimals")
    void bigDecimal(BigDecimal offset, Precision precision, List<Integer> dateElements) {
        final int[] dateElementsArray =
                dateElements.stream().mapToInt(anInt -> anInt).toArray();
        final DateTime dateTime = new DateTime(offset, dateElementsArray);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        logger.warn(
                "TEST: {}, offset: {}, precision: {}, dateElements: {}, actualDateTime: {}, expectedDateTime: {}",
                dateTime.getDateTime().equals(normalizedDateTime),
                offset,
                precision,
                dateElements,
                normalizedDateTime,
                dateTime.getDateTime());

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    @Test
    void nullBigDecimalOffset() {
        var digits =
                DST_2023_10_26_22_12_0_INTS.stream().mapToInt(anInt -> anInt).toArray();
        assertThrows(CqlException.class, () -> new DateTime(null, digits));
    }

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");
    private static final ZoneId REGINA =
            ZoneId.of("America/Regina"); // Saskatchewan does not have standard time (non-DST) all year round
    private static final LocalDateTime DST_2023_11_01 = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0);
    private static final LocalDateTime NON_DST_2023_11_13 = LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0);

    private static Object[][] timeZones() {
        return new Object[][] {
            {UTC, DST_2023_11_01}, {MONTREAL, DST_2023_11_01}, {REGINA, DST_2023_11_01},
            {UTC, NON_DST_2023_11_13}, {MONTREAL, NON_DST_2023_11_13}, {REGINA, NON_DST_2023_11_13}
        };
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void bigDecimalWithCustomTimezoneAndNow(ZoneId zoneId, LocalDateTime now) {
        final ZoneOffset currentOffsetForMyZone = zoneId.getRules().getOffset(now);
        final BigDecimal offset = TemporalHelper.zoneToOffset(currentOffsetForMyZone);

        final int[] dateElementsArray =
                DST_2023_10_26_22_12_0_INTS.stream().mapToInt(anInt -> anInt).toArray();
        final DateTime dateTime = new DateTime(offset, dateElementsArray);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(Precision.HOUR);

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    @Test
    void dateTimeEquals() {
        var dateTime = new DateTime(BigDecimal.ONE, 2020);

        assertEquals(dateTime, dateTime);
        assertNotEquals(null, dateTime);
        assertNotEquals(1, dateTime);

        var dateTime2 = new DateTime(BigDecimal.ONE, 2020);

        // identical constructors
        assertEquals(dateTime, dateTime2);

        // varying offset
        dateTime2 = new DateTime(BigDecimal.valueOf(2l), 2020);
        assertNotEquals(dateTime, dateTime2);

        // varying year
        dateTime2 = new DateTime(BigDecimal.ONE, 2021);
        assertNotEquals(dateTime, dateTime2);

        // varying precision
        dateTime2 = new DateTime(BigDecimal.ONE, 2020, 03);
        assertNotEquals(dateTime, dateTime2);

        // TODO: It'd be good to extend these across different types of constructors, for example, from a string
    }

    @Test
    void roundToHigherPrecision() {
        var offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45+11:00");
        var dateTime = new DateTime(offsetDateTime, Precision.MINUTE);

        var roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MILLISECOND, false);
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MILLISECOND, true);
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.SECOND, false);
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.SECOND, true);
        assertEquals("2025-07-15T10:30+11:00", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());
    }

    @Test
    void roundToSamePrecision() {
        var offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45+12:45");
        var dateTime = new DateTime(offsetDateTime, Precision.MINUTE);

        var roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MINUTE, false);
        assertEquals("2025-07-15T10:30+12:45", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MINUTE, true);
        assertEquals("2025-07-15T10:30+12:45", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MINUTE, roundedDateTime.getPrecision());
    }

    @Test
    void roundToLowerPrecision() {
        var offsetDateTime = OffsetDateTime.parse("2025-07-15T10:30:45-04:30");
        var dateTime = new DateTime(offsetDateTime, Precision.MINUTE);

        var roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.HOUR, false);
        assertEquals("2025-07-15T10:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.HOUR, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.HOUR, true);
        assertEquals("2025-07-15T11:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.HOUR, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.DAY, false);
        assertEquals("2025-07-15T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.DAY, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.DAY, true);
        assertEquals("2025-07-16T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.DAY, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.WEEK, false);
        assertEquals("2025-07-15T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.DAY, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.WEEK, true);
        assertEquals("2025-07-16T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.DAY, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MONTH, false);
        assertEquals("2025-07-01T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MONTH, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.MONTH, true);
        assertEquals("2025-08-01T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.MONTH, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.YEAR, false);
        assertEquals("2025-01-01T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.YEAR, roundedDateTime.getPrecision());

        roundedDateTime = (DateTime) dateTime.roundToPrecision(Precision.YEAR, true);
        assertEquals("2026-01-01T00:00-04:30", roundedDateTime.getDateTime().toString());
        assertEquals(Precision.YEAR, roundedDateTime.getPrecision());
    }
}
