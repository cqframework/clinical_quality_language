package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.cqframework.cql.shared.BigDecimal

class IntervalHelperTest {
    @Test
    fun findNonNullBoundary() {
        val intervals =
            listOf(
                null,
                Interval(5.toCqlInteger(), true, null, true),
                Interval(null, false, 6.toCqlInteger(), true),
            )
        assertEquals(5.toCqlInteger(), IntervalHelper.findNonNullBoundary(intervals))
    }

    @Test
    fun quantityFromCoarsestPrecisionOfBoundaries() {
        var intervals =
            listOf(
                null,
                Interval(
                    BigDecimal("1.12").toCqlDecimal(),
                    true,
                    BigDecimal("1.1234").toCqlDecimal(),
                    true,
                ),
            )
        var quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        assertEquals(1, quantity.value!!.toInt())
        assertEquals(2, quantity.value!!.scale())
        assertTrue(Quantity.isDefaultUnit(quantity.unit))

        intervals =
            listOf(
                Interval(null, false, Quantity().withValue(BigDecimal("1.123")).withUnit("g"), true)
            )
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        assertEquals(1, quantity.value!!.toInt())
        assertEquals(3, quantity.value!!.scale())
        assertEquals("g", quantity.unit)

        intervals = listOf(Interval(Date("2025-07-15"), true, Date("2025-12"), true))
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        assertEquals(1, quantity.value!!.toInt())
        assertEquals(0, quantity.value!!.scale())
        assertEquals("month", quantity.unit)

        intervals = listOf(Interval(10.toCqlInteger(), true, null, true))
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        assertEquals(1, quantity.value!!.toInt())
        assertEquals(0, quantity.value!!.scale())
        assertTrue(Quantity.isDefaultUnit(quantity.unit))
    }

    @Test
    fun isQuantityCompatibleWithBoundaries() {
        val quantityWithDefaultUnit = Quantity().withValue(BigDecimal("1.123")).withDefaultUnit()
        val gramsQuantity = Quantity().withValue(BigDecimal("1.123")).withUnit("g")
        val monthsQuantity = Quantity().withValue(BigDecimal("10")).withUnit("month")

        var intervals = listOf<Interval?>(Interval(10.toCqlInteger(), true, null, false))
        assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))

        intervals = listOf(Interval(1L.toCqlLong(), true, null, true))
        assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))

        intervals = listOf(null, Interval(BigDecimal("1.1").toCqlDecimal(), true, null, true))
        assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))

        intervals =
            listOf(
                Interval(Quantity().withValue(BigDecimal("1.1")).withUnit("g"), true, null, true)
            )
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))
        assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )

        intervals = listOf(Interval(null, false, Date("2025-12"), true))
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(monthsQuantity, intervals))
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))
        assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )

        intervals = listOf(null)
        assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals))
    }
}
