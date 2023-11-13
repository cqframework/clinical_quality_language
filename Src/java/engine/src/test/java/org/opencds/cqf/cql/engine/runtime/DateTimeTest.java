package org.opencds.cqf.cql.engine.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.TimeZone;

import static org.testng.Assert.*;

public class DateTimeTest {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeTest.class);

    private static final LocalDateTime DST_2023_10_26_22_12_0 = LocalDateTime.of(2023, Month.OCTOBER, 26, 22, 12, 0);
    private static final LocalDateTime DST_2023_11_03_02_52_0 = LocalDateTime.of(2023, Month.NOVEMBER, 3, 2, 52, 0);
    // This is OUTSIDE of Daylight Savings Time
    private static final LocalDateTime NON_DST_2024_02_27_07_28_0 = LocalDateTime.of(2024, Month.FEBRUARY, 27, 7, 28, 0);
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

    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_EASTERN = ZoneOffset.of("-04:00");
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_EASTERN = ZoneOffset.of("-05:00");
    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_MOUNTAIN = ZoneOffset.of("-06:00");
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN = ZoneOffset.of("-07:00");
    private static final ZoneOffset DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = ZoneOffset.of("-02:30");
    // This offset doesn't exist for an ZoneID
    private static final ZoneOffset NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = ZoneOffset.of("-03:30");

    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN = toBigDecimal(DST_OFFSET_NORTH_AMERICA_EASTERN);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN = toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_EASTERN);
    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN = toBigDecimal(DST_OFFSET_NORTH_AMERICA_MOUNTAIN);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN = toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN);
    private static final BigDecimal DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = toBigDecimal(DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND);
    private static final BigDecimal NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = toBigDecimal(NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND);

    private static BigDecimal toBigDecimal(ZoneOffset zoneOffset) {
        final long offsetSeconds = zoneOffset.getLong(ChronoField.OFFSET_SECONDS);
        final BigDecimal offsetMinutes = BigDecimal.valueOf(offsetSeconds)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
        return offsetMinutes.divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
    }

    private static List<Integer> toList(LocalDateTime localDateTime) {
        return List.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
    }

    @DataProvider
    private static Object[][] dateStrings() {
        return new Object[][]{
                {DST_2023_10_26_22_12_0_STRING, null, Precision.HOUR},
                {DST_2023_10_26_22_12_0_STRING, ZoneOffset.UTC, Precision.HOUR},
                {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {DST_2023_10_26_22_12_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
                {DST_2023_10_26_22_12_0_STRING, null, Precision.MILLISECOND},
                {DST_2023_11_03_02_52_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND},
                {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
                {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {DST_2023_11_03_02_52_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
                {NON_DST_2024_02_27_07_28_0_STRING, null, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0_STRING, ZoneOffset.UTC, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0_STRING, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
                {DST_2024_06_15_23_32_0_STRING, null, Precision.MILLISECOND},
                {DST_2024_06_15_23_32_0_STRING, ZoneOffset.UTC, Precision.MILLISECOND},
                {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
                {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {DST_2024_06_15_23_32_0_STRING, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND}
        };
    }

    @Test(dataProvider = "dateStrings")
    void testDateStrings(String dateString, ZoneOffset zoneOffset, Precision precision) {
        final DateTime dateTime = new DateTime(dateString, zoneOffset);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    @DataProvider
    private static Object[][] dateStringsOtherZoneId() {
        return new Object[][]{
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_EASTERN, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_EASTERN, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_EASTERN, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_EASTERN, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
                {DST_2023_10_26_22_12_0, DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, NON_DST_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
                {NON_DST_2024_02_27_07_28_0, NON_DST_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, NON_DST_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
        };
    }

    @Test(dataProvider = "dateStringsOtherZoneId")
    void testDateStringsOtherZoneId(LocalDateTime localDateTime, ZoneOffset zoneOffsetInit, ZoneOffset zonedOffsetGetNormalized, Precision precision) {
        final OffsetDateTime expectedOffsetDateTime = OffsetDateTime.of(localDateTime.minusSeconds(zoneOffsetInit.getTotalSeconds() - zonedOffsetGetNormalized.getTotalSeconds()), zonedOffsetGetNormalized);
        final DateTime dateTime = new DateTime(FORMATTER.format(localDateTime), zoneOffsetInit);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision, zonedOffsetGetNormalized);

        assertEquals(normalizedDateTime, expectedOffsetDateTime);
    }

    @DataProvider
    private static Object[][] offsetPrecisions() {
        return new Object[][]{
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

    @Test(dataProvider = "offsetPrecisions")
    void testOffsetPrecisions(LocalDateTime localDateTime, ZoneOffset zoneOffset, Precision precision) {
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);
        final DateTime dateTimeNoPrecision = new DateTime(offsetDateTime);
        final DateTime dateTimePrecision = new DateTime(offsetDateTime, precision);

        final OffsetDateTime normalizedDateTimeNoPrecision = dateTimeNoPrecision.getNormalized(precision);
        final OffsetDateTime normalizedDateTimePrecision = dateTimePrecision.getNormalized(precision);

        assertEquals(normalizedDateTimeNoPrecision, dateTimeNoPrecision.getDateTime());
        assertEquals(normalizedDateTimePrecision, dateTimePrecision.getDateTime());
    }

    @DataProvider
    private static Object[][] bigDecimals() {
        return new Object[][]{
                {null, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
                {BigDecimal.ZERO, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, DST_2023_10_26_22_12_0_INTS},
                {null, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
                {BigDecimal.ZERO, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, DST_2023_11_03_02_52_0_INTS},
                {null, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
                {BigDecimal.ZERO, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
                {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
                {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
                {NON_DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, NON_DST_2024_02_27_07_28_0_INTS},
                {null, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
                {BigDecimal.ZERO, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, DST_2024_06_15_23_32_0_INTS},
                {DST_BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, DST_2024_06_15_23_32_0_INTS},
        };
    }

    @Test(dataProvider = "bigDecimals")
    void testBigDecimal(BigDecimal offset, Precision precision, List<Integer> dateElements) {
        final int[] dateElementsArray = dateElements.stream().mapToInt(anInt -> anInt).toArray();
        final DateTime dateTime = new DateTime(offset, dateElementsArray);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        logger.warn("TEST: {}, offset: {}, precision: {}, dateElements: {}, actualDateTime: {}, expectedDateTime: {}", dateTime.getDateTime().equals(normalizedDateTime), offset, precision, dateElements, normalizedDateTime, dateTime.getDateTime());

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");
    private static final ZoneId REGINA = ZoneId.of("America/Regina");
    private static final LocalDateTime _2023_11_01 = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0);
    private static final LocalDateTime _2023_11_13 = LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0);
    @DataProvider
    private static Object[][] timeZones() {
        return new Object[][]{
                {UTC, _2023_11_01}, {MONTREAL, _2023_11_01}, {REGINA, _2023_11_01},
                {UTC, _2023_11_13}, {MONTREAL, _2023_11_13}, {REGINA, _2023_11_13}
        };
    }

    @Test(dataProvider = "timeZones")
    void testBigDecimalWithCustomTimezoneAndNow(ZoneId zoneId, LocalDateTime now) {
        final Instant instant = Instant.now(); //can be LocalDateTime
        final TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        final ZoneOffset currentOffsetForMyZone = zoneId.getRules().getOffset(instant);
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(now, currentOffsetForMyZone);

        final BigDecimal offset = null;
        final Precision precision = Precision.HOUR;
        final List<Integer> dateElements = DST_2023_10_26_22_12_0_INTS;

        final int[] dateElementsArray = dateElements.stream().mapToInt(anInt -> anInt).toArray();
        final DateTime dateTime = new DateTime(offset, timeZone, offsetDateTime, dateElementsArray);

        // LUKETODO:  how do I pass this down before I call the DateTime constructor?

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        logger.warn("TEST: {}, offset: {}, precision: {}, dateElements: {}, actualDateTime: {}, expectedDateTime: {}", dateTime.getDateTime().equals(normalizedDateTime), offset, precision, dateElements, normalizedDateTime, dateTime.getDateTime());

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }
}