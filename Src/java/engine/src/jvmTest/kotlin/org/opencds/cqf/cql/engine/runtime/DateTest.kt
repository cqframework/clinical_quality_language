package org.opencds.cqf.cql.engine.runtime

import java.time.LocalDate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DateTest {
    @Test
    fun roundToHigherPrecision() {
        val localDate = LocalDate.parse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        val roundedDate = date.roundToPrecision(Precision.MILLISECOND, false) as Date
        Assertions.assertEquals("2025-07-01", roundedDate.date.toString())
        Assertions.assertEquals(Precision.MONTH, roundedDate.precision)
    }

    @Test
    fun roundToSamePrecision() {
        val localDate = LocalDate.parse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        var roundedDate = date.roundToPrecision(Precision.MONTH, false) as Date
        Assertions.assertEquals("2025-07-01", roundedDate.date.toString())
        Assertions.assertEquals(Precision.MONTH, roundedDate.precision)

        roundedDate = date.roundToPrecision(Precision.MONTH, true) as Date
        Assertions.assertEquals("2025-07-01", roundedDate.date.toString())
        Assertions.assertEquals(Precision.MONTH, roundedDate.precision)
    }

    @Test
    fun roundToLowerPrecision() {
        val localDate = LocalDate.parse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        var roundedDate = date.roundToPrecision(Precision.YEAR, false) as Date
        Assertions.assertEquals("2025-01-01", roundedDate.date.toString())
        Assertions.assertEquals(Precision.YEAR, roundedDate.precision)

        roundedDate = date.roundToPrecision(Precision.YEAR, true) as Date
        Assertions.assertEquals("2026-01-01", roundedDate.date.toString())
        Assertions.assertEquals(Precision.YEAR, roundedDate.precision)
    }
}
