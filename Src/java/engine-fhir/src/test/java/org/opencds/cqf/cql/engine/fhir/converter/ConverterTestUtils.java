package org.opencds.cqf.cql.engine.fhir.converter;

import org.testng.annotations.DataProvider;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class ConverterTestUtils {
    static final LocalDateTime DST_2022_11_01 = LocalDateTime.of(2022, Month.NOVEMBER, 1, 0, 0, 0);
    static final LocalDateTime DST_2023_11_01 = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0);
    static final LocalDateTime DST_2023_11_03 = LocalDateTime.of(2023, Month.NOVEMBER, 3, 0, 0, 0);
    static final LocalDateTime NON_DST_2022_01_01 = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0, 0);
    static final LocalDateTime NON_DST_2023_01_01 = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0, 0);
    static final LocalDateTime NON_DST_2022_11_10 = LocalDateTime.of(2022, Month.NOVEMBER, 10, 0, 0, 0);
    static final LocalDateTime NON_DST_2023_11_10 = LocalDateTime.of(2023, Month.NOVEMBER, 10, 0, 0, 0);
    static final LocalDateTime NON_DST_2023_11_14 = LocalDateTime.of(2023, Month.NOVEMBER, 14, 0, 0, 0);
    static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @DataProvider
    static Object[][] dateTimes() {
        return new Object[][] {{DST_2023_11_01, DST_2023_11_01, DST_2023_11_03}, {NON_DST_2023_11_14, NON_DST_2023_11_10, NON_DST_2023_11_14}, {NON_DST_2023_11_14, DST_2023_11_01, DST_2023_11_03}, {DST_2023_11_01, NON_DST_2023_11_10, NON_DST_2023_11_14}};
    }

    @DataProvider
    static Object[][] startAndEndTimes() {
        return new Object[][] {{DST_2023_11_01, DST_2023_11_03}, {NON_DST_2023_11_10, NON_DST_2023_11_14}, {DST_2023_11_01, DST_2023_11_03}, {NON_DST_2023_11_10, NON_DST_2023_11_14}, {DST_2022_11_01, DST_2023_11_03}, {NON_DST_2022_11_10, NON_DST_2023_11_10}, {NON_DST_2022_01_01, NON_DST_2023_01_01}};
    }

    @DataProvider
    static Object[][] startAndEndYears() {
        return new Object[][] {{DST_2022_11_01, 2019, 2020}, {NON_DST_2023_11_14, 2019, 2020},{DST_2022_11_01, 2018, 2022}, {NON_DST_2023_11_14, 2018, 2022}};
    }
    @DataProvider
    static Object[][] nowsAndEvaluationTimes() {
        return new Object[][] {{NON_DST_2022_01_01, NON_DST_2023_01_01}, {DST_2022_11_01, NON_DST_2023_01_01}, {NON_DST_2022_11_10, NON_DST_2023_01_01}};
    }
}
