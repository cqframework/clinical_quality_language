package org.opencds.cqf.cql.engine.fhir.converter

import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter

object ConverterTestUtils {
    val DST_2022_11_01: LocalDateTime = LocalDateTime.of(2022, Month.NOVEMBER, 1, 0, 0, 0)
    val DST_2023_11_01: LocalDateTime = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0)
    val DST_2023_11_03: LocalDateTime = LocalDateTime.of(2023, Month.NOVEMBER, 3, 0, 0, 0)
    val NON_DST_2022_01_01: LocalDateTime = LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0, 0)
    val NON_DST_2023_01_01: LocalDateTime = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0, 0)
    val NON_DST_2022_11_10: LocalDateTime = LocalDateTime.of(2022, Month.NOVEMBER, 10, 0, 0, 0)
    val NON_DST_2023_11_10: LocalDateTime = LocalDateTime.of(2023, Month.NOVEMBER, 10, 0, 0, 0)
    val NON_DST_2023_11_14: LocalDateTime = LocalDateTime.of(2023, Month.NOVEMBER, 14, 0, 0, 0)
    @JvmField val YYYY_MM_DD: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @JvmStatic
    fun dateTimes(): Array<Array<Any?>?> {
        return arrayOf(
            arrayOf(DST_2023_11_01, DST_2023_11_01, DST_2023_11_03),
            arrayOf(NON_DST_2023_11_14, NON_DST_2023_11_10, NON_DST_2023_11_14),
            arrayOf(NON_DST_2023_11_14, DST_2023_11_01, DST_2023_11_03),
            arrayOf(DST_2023_11_01, NON_DST_2023_11_10, NON_DST_2023_11_14),
        )
    }

    @JvmStatic
    fun startAndEndTimes(): Array<Array<Any?>?> {
        return arrayOf(
            arrayOf(DST_2023_11_01, DST_2023_11_03),
            arrayOf(NON_DST_2023_11_10, NON_DST_2023_11_14),
            arrayOf(DST_2023_11_01, DST_2023_11_03),
            arrayOf(NON_DST_2023_11_10, NON_DST_2023_11_14),
            arrayOf(DST_2022_11_01, DST_2023_11_03),
            arrayOf(NON_DST_2022_11_10, NON_DST_2023_11_10),
            arrayOf(NON_DST_2022_01_01, NON_DST_2023_01_01),
        )
    }

    @JvmStatic
    fun startAndEndYears(): Array<Array<Any?>?> {
        return arrayOf(
            arrayOf(DST_2022_11_01, 2019, 2020),
            arrayOf(NON_DST_2023_11_14, 2019, 2020),
            arrayOf(DST_2022_11_01, 2018, 2022),
            arrayOf(NON_DST_2023_11_14, 2018, 2022),
        )
    }

    @JvmStatic
    fun nowsAndEvaluationTimes(): Array<Array<Any?>?> {
        return arrayOf(
            arrayOf(NON_DST_2022_01_01, NON_DST_2023_01_01),
            arrayOf(DST_2022_11_01, NON_DST_2023_01_01),
            arrayOf(NON_DST_2022_11_10, NON_DST_2023_01_01),
        )
    }
}
