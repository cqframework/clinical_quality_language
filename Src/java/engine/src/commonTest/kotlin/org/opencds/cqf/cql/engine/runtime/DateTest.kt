package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.util.localDateParse

class DateTest {
    @Test
    fun roundToHigherPrecision() {
        val localDate = localDateParse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        val roundedDate = date.roundToPrecision(Precision.MILLISECOND, false) as Date
        assertEquals("2025-07-01", roundedDate.date.toString())
        assertEquals(Precision.MONTH, roundedDate.precision)
    }

    @Test
    fun roundToSamePrecision() {
        val localDate = localDateParse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        var roundedDate = date.roundToPrecision(Precision.MONTH, false) as Date
        assertEquals("2025-07-01", roundedDate.date.toString())
        assertEquals(Precision.MONTH, roundedDate.precision)

        roundedDate = date.roundToPrecision(Precision.MONTH, true) as Date
        assertEquals("2025-07-01", roundedDate.date.toString())
        assertEquals(Precision.MONTH, roundedDate.precision)
    }

    @Test
    fun roundToLowerPrecision() {
        val localDate = localDateParse("2025-07-01")
        val date = Date(localDate, Precision.MONTH)

        var roundedDate = date.roundToPrecision(Precision.YEAR, false) as Date
        assertEquals("2025-01-01", roundedDate.date.toString())
        assertEquals(Precision.YEAR, roundedDate.precision)

        roundedDate = date.roundToPrecision(Precision.YEAR, true) as Date
        assertEquals("2026-01-01", roundedDate.date.toString())
        assertEquals(Precision.YEAR, roundedDate.precision)
    }
}
