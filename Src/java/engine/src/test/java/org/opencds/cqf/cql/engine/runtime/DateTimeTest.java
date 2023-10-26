package org.opencds.cqf.cql.engine.runtime;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;

import static org.testng.Assert.*;

public class DateTimeTest {
    private static final LocalDateTime LDT_2023_10_26_22_12_0 = LocalDateTime.of(2023, Month.OCTOBER, 26, 22, 12, 0);
    private static final LocalDateTime LDT_2023_11_03_02_52_0 = LocalDateTime.of(2023, Month.NOVEMBER, 3, 2, 52, 0);
    // This is OUTSIDE of Daylight Savings Time
    private static final LocalDateTime LDT_2024_02_27_07_28_0 = LocalDateTime.of(2024, Month.FEBRUARY, 27, 7, 28, 0);
    private static final LocalDateTime LDT_2024_06_15_23_32_0 = LocalDateTime.of(2024, Month.JULY, 15, 23, 32, 0);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String _2023_10_26 = FORMATTER.format(LDT_2023_10_26_22_12_0);
    private static final String _2023_11_03 = FORMATTER.format(LDT_2023_11_03_02_52_0);
    private static final String _2024_02_27 = FORMATTER.format(LDT_2024_02_27_07_28_0);
    private static final String _2024_06_15 = FORMATTER.format(LDT_2024_06_15_23_32_0);

    private static final List<Integer> _2023_10_26_22_12_0 = toList(LDT_2024_06_15_23_32_0);

    private static final List<Integer> _2023_11_03_02_52_0 = toList(LDT_2023_11_03_02_52_0);
    private static final List<Integer> _2024_02_27_07_28_0 = toList(LDT_2024_02_27_07_28_0);
    private static final List<Integer> _2024_06_15_23_32_0 = toList(LDT_2024_06_15_23_32_0);

    // LUKETODO:  change each of these offsets when dealing with standard (non-DST) time
    private static final ZoneOffset OFFSET_NORTH_AMERICA_EASTERN = ZoneOffset.of("-04:00");
    private static final ZoneOffset OFFSET_NORTH_AMERICA_MOUNTAIN = ZoneOffset.of("-06:00");
    private static final ZoneOffset OFFSET_NORTH_AMERICA_NEWFOUNDLAND = ZoneOffset.of("-02:30");
    private static final ZoneOffset OFFSET_NORTH_AMERICA_NEWFOUNDLAND_DST = ZoneOffset.of("-03:30");

    private static final BigDecimal BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN = toBigDecimal(OFFSET_NORTH_AMERICA_EASTERN);
    private static final BigDecimal BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN = toBigDecimal(OFFSET_NORTH_AMERICA_MOUNTAIN);
    private static final BigDecimal BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND = toBigDecimal(OFFSET_NORTH_AMERICA_NEWFOUNDLAND);
    private static final BigDecimal BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND_DST = toBigDecimal(OFFSET_NORTH_AMERICA_NEWFOUNDLAND_DST);

    private static BigDecimal toBigDecimal(ZoneOffset zoneOffset) {
        final long offsetSeconds = zoneOffset.getLong(ChronoField.OFFSET_SECONDS);
        final BigDecimal offsetMinutes = BigDecimal.valueOf(offsetSeconds)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
        return offsetMinutes .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);
    }

    private static List<Integer> toList(LocalDateTime localDateTime) {
        return List.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
    }

    @DataProvider
    private static Object[][] dateStrings() {
        return new Object[][] {
                {_2023_10_26, null, Precision.HOUR},
                {_2023_10_26, ZoneOffset.UTC, Precision.HOUR},
                {_2023_10_26, OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {_2023_10_26, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {_2023_10_26, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
                {_2023_10_26, null, Precision.MILLISECOND},
                {_2023_11_03, ZoneOffset.UTC, Precision.MILLISECOND},
                {_2023_11_03, OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
                {_2023_11_03, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {_2023_11_03, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
                {_2024_02_27, null, Precision.HOUR},
                {_2024_02_27, ZoneOffset.UTC, Precision.HOUR},
                {_2024_02_27, OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
                {_2024_02_27, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {_2024_02_27, OFFSET_NORTH_AMERICA_NEWFOUNDLAND_DST, Precision.HOUR},
                {_2024_06_15, null, Precision.MILLISECOND},
                {_2024_06_15, ZoneOffset.UTC, Precision.MILLISECOND},
                {_2024_06_15, OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
                {_2024_06_15, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
                {_2024_06_15, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND}
        };
    }

    @Test(dataProvider = "dateStrings")
    void testDateStrings(String dateString, ZoneOffset zoneOffset, Precision precision) {
        final DateTime dateTime = new DateTime(dateString, zoneOffset);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    @DataProvider
    private static Object[][] offsetPrecisions() {
        return new Object[][] {
//                {LDT_2023_10_26_22_12_0, ZoneOffset.UTC, Precision.HOUR},
//                {LDT_2023_10_26_22_12_0, OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
//                {LDT_2023_10_26_22_12_0, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
//                {LDT_2023_10_26_22_12_0, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
//                {LDT_2023_11_03_02_52_0, ZoneOffset.UTC, Precision.MILLISECOND},
//                {LDT_2023_11_03_02_52_0, OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
//                {LDT_2023_11_03_02_52_0, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
//                {LDT_2023_11_03_02_52_0, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
//                {LDT_2024_02_27_07_28_0, ZoneOffset.UTC, Precision.HOUR},
                {LDT_2024_02_27_07_28_0, OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR},
//                {LDT_2024_02_27_07_28_0, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR},
                {LDT_2024_02_27_07_28_0, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR},
//                {LDT_2024_06_15_23_32_0, ZoneOffset.UTC, Precision.MILLISECOND},
//                {LDT_2024_06_15_23_32_0, OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND},
//                {LDT_2024_06_15_23_32_0, OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND},
//                {LDT_2024_06_15_23_32_0, OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND},
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
        return new Object[][] {
                {null, Precision.HOUR, _2023_10_26_22_12_0},
                {BigDecimal.ZERO, Precision.HOUR, _2023_10_26_22_12_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, _2023_10_26_22_12_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, _2023_10_26_22_12_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, _2023_10_26_22_12_0},
                {null, Precision.MILLISECOND, _2023_11_03_02_52_0},
                {BigDecimal.ZERO, Precision.MILLISECOND, _2023_11_03_02_52_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, _2023_11_03_02_52_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.MILLISECOND, _2023_11_03_02_52_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, _2023_11_03_02_52_0},
                {null, Precision.HOUR, _2024_02_27_07_28_0},
                {BigDecimal.ZERO, Precision.HOUR, _2024_02_27_07_28_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.HOUR, _2024_02_27_07_28_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, _2024_02_27_07_28_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.HOUR, _2024_02_27_07_28_0},
                {null, Precision.MILLISECOND, _2024_06_15_23_32_0},
                {BigDecimal.ZERO, Precision.MILLISECOND, _2024_06_15_23_32_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_EASTERN, Precision.MILLISECOND, _2024_06_15_23_32_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_MOUNTAIN, Precision.HOUR, _2024_06_15_23_32_0},
                {BIG_DECIMAL_OFFSET_NORTH_AMERICA_NEWFOUNDLAND, Precision.MILLISECOND, _2024_06_15_23_32_0},
        };
    }

    @Test(dataProvider = "bigDecimals")
    void testBigDecimal(BigDecimal offset, Precision precision, List<Integer> dateElements) {
        final int[] dateElementsArray = dateElements.stream().mapToInt(anInt -> anInt).toArray();
        final DateTime dateTime = new DateTime(offset, dateElementsArray);

        final OffsetDateTime normalizedDateTime = dateTime.getNormalized(precision);

        assertEquals(normalizedDateTime, dateTime.getDateTime());
    }

    // LUKETODO:  consider 2 param getNormalized
}