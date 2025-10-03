package org.opencds.cqf.cql.engine.runtime

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class IntervalHelperTest {
    @Test
    fun findNonNullBoundary() {
        val intervals = listOf(null, Interval(5, true, null, true), Interval(null, false, 6, true))
        Assertions.assertEquals(5, IntervalHelper.findNonNullBoundary(intervals))
    }

    @Test
    fun quantityFromCoarsestPrecisionOfBoundaries() {
        var intervals = listOf(null, Interval(BigDecimal("1.12"), true, BigDecimal("1.1234"), true))
        var quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        Assertions.assertEquals(1, quantity.value!!.toInt())
        Assertions.assertEquals(2, quantity.value!!.scale())
        Assertions.assertTrue(Quantity.isDefaultUnit(quantity.unit))

        intervals =
            listOf<Interval?>(
                Interval(null, false, Quantity().withValue(BigDecimal("1.123")).withUnit("g"), true)
            )
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        Assertions.assertEquals(1, quantity.value!!.toInt())
        Assertions.assertEquals(3, quantity.value!!.scale())
        Assertions.assertEquals("g", quantity.unit)

        intervals = listOf<Interval?>(Interval(Date("2025-07-15"), true, Date("2025-12"), true))
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        Assertions.assertEquals(1, quantity.value!!.toInt())
        Assertions.assertEquals(0, quantity.value!!.scale())
        Assertions.assertEquals("month", quantity.unit)

        intervals = listOf<Interval?>(Interval(10, true, null, true))
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals)
        Assertions.assertEquals(1, quantity.value!!.toInt())
        Assertions.assertEquals(0, quantity.value!!.scale())
        Assertions.assertTrue(Quantity.isDefaultUnit(quantity.unit))
    }

    @Test
    fun isQuantityCompatibleWithBoundaries() {
        val quantityWithDefaultUnit = Quantity().withValue(BigDecimal("1.123")).withDefaultUnit()
        val gramsQuantity = Quantity().withValue(BigDecimal("1.123")).withUnit("g")
        val monthsQuantity = Quantity().withValue(BigDecimal("10")).withUnit("month")

        var intervals = listOf<Interval?>(Interval(10, true, null, false))
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )

        intervals = listOf<Interval?>(Interval(1L, true, null, true))
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )

        intervals = listOf(null, Interval(BigDecimal("1.1"), true, null, true))
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )

        intervals =
            listOf<Interval?>(
                Interval(Quantity().withValue(BigDecimal("1.1")).withUnit("g"), true, null, true)
            )
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )

        intervals = listOf<Interval?>(Interval(null, false, Date("2025-12"), true))
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(monthsQuantity, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )
        Assertions.assertFalse(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )

        intervals = listOf(null as Interval?)
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals)
        )
        Assertions.assertTrue(
            IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals)
        )
    }
}
