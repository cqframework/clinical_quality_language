package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidPrecision

/**
 * [#1965](https://github.com/cqframework/clinical_quality_language/issues/1965): a Date can't be
 * compared at a time precision, and a Time can't be compared at a date precision.
 */
class ComparisonPrecisionTest {
    @Test
    fun dateRejectsTimePrecisions() {
        val left = Date(2014, 1, 1)
        val right = Date(2013, 1, 1)
        for (p in
            listOf(Precision.HOUR, Precision.MINUTE, Precision.SECOND, Precision.MILLISECOND)) {
            assertFailsWith<InvalidPrecision>(p.name) { left.compareToPrecision(right, p) }
        }
        assertEquals(1, left.compareToPrecision(right, Precision.YEAR))
        assertEquals(1, left.compareToPrecision(right, Precision.DAY))
    }

    @Test
    fun timeRejectsDatePrecisions() {
        val left = Time(10, 0)
        val right = Time(9, 0)
        for (p in listOf(Precision.YEAR, Precision.MONTH, Precision.WEEK, Precision.DAY)) {
            assertFailsWith<InvalidPrecision>(p.name) { left.compareToPrecision(right, p) }
        }
        assertEquals(1, left.compareToPrecision(right, Precision.HOUR))
    }

    @Test
    fun dateTimeAcceptsAllPrecisions() {
        val left = DateTime(BigDecimal("0"), 2014, 1, 1, 10, 0)
        val right = DateTime(BigDecimal("0"), 2014, 1, 1, 9, 0)
        assertEquals(0, left.compareToPrecision(right, Precision.DAY))
        assertEquals(1, left.compareToPrecision(right, Precision.HOUR))
    }
}
