package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.exception.InvalidInterval

class IntervalTest {
    @Test
    fun startAfterEndIsRejectedWithoutState() {
        assertFailsWith<InvalidInterval> {
            Interval(3.toCqlInteger(), true, 2.toCqlInteger(), true)
        }
        assertFailsWith<InvalidInterval> {
            Interval(3.toCqlInteger(), true, 2.toCqlInteger(), true, true)
        }
        assertFailsWith<InvalidInterval> {
            Interval(BigDecimal("2.5").toCqlDecimal(), true, BigDecimal("1.5").toCqlDecimal(), true)
        }
        assertFailsWith<InvalidInterval> {
            Interval(Quantity(BigDecimal("5"), "mg"), true, Quantity(BigDecimal("2"), "mg"), true)
        }
    }

    @Test
    fun openBoundariesAreCheckedOnTheirStartAndEnd() {
        // Interval(2, 3) has start 3 and end 2
        assertFailsWith<InvalidInterval> {
            Interval(2.toCqlInteger(), false, 3.toCqlInteger(), false)
        }
        val interval = Interval(2.toCqlInteger(), false, 4.toCqlInteger(), false)
        assertEquals(3.toCqlInteger(), interval.start)
        assertEquals(3.toCqlInteger(), interval.end)
    }

    @Test
    fun withLowAndWithHighAreChecked() {
        val interval = Interval(1.toCqlInteger(), true, 5.toCqlInteger(), true)
        assertFailsWith<InvalidInterval> { interval.withLow(6.toCqlInteger()) }
        assertFailsWith<InvalidInterval> { interval.withHigh(0.toCqlInteger()) }
        assertEquals(5.toCqlInteger(), interval.withLow(5.toCqlInteger()).start)
    }

    @Test
    fun equalBoundariesAndNullBoundariesAreAllowed() {
        val point = Interval(2.toCqlInteger(), true, 2.toCqlInteger(), true)
        assertEquals(2.toCqlInteger(), point.start)
        assertEquals(2.toCqlInteger(), point.end)
        Interval(null, true, 2.toCqlInteger(), true)
        Interval(2.toCqlInteger(), true, null, true)
    }

    @Test
    fun quantitiesInDifferentUnitsAreLeftToTheStateConstructor() {
        // Without a state there is no UCUM service to compare 'g' with 'mg', so the primary
        // constructor accepts the interval; the constructor that takes a state checks it.
        val interval =
            Interval(Quantity(BigDecimal("1"), "g"), true, Quantity(BigDecimal("2"), "mg"), true)
        assertEquals("g", (interval.low as Quantity).unit)
    }
}
