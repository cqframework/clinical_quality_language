package org.opencds.cqf.cql.engine.runtime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class IntervalHelperTest {

    @Test
    void findNonNullBoundary() {
        var intervals = Arrays.asList(null, new Interval(5, true, null, true), new Interval(null, false, 6, true));
        assertEquals(5, IntervalHelper.findNonNullBoundary(intervals));
    }

    @Test
    void quantityFromCoarsestPrecisionOfBoundaries() {
        var intervals = Arrays.asList(null, new Interval(new BigDecimal("1.12"), true, new BigDecimal("1.1234"), true));
        var quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals);
        assertEquals(1, quantity.getValue().intValue());
        assertEquals(2, quantity.getValue().scale());
        assertTrue(Quantity.isDefaultUnit(quantity.getUnit()));

        intervals = Arrays.asList(new Interval(
                null, false, new Quantity().withValue(new BigDecimal("1.123")).withUnit("g"), true));
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals);
        assertEquals(1, quantity.getValue().intValue());
        assertEquals(3, quantity.getValue().scale());
        assertEquals("g", quantity.getUnit());

        intervals = Arrays.asList(new Interval(new Date("2025-07-15"), true, new Date("2025-12"), true));
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals);
        assertEquals(1, quantity.getValue().intValue());
        assertEquals(0, quantity.getValue().scale());
        assertEquals("month", quantity.getUnit());

        intervals = Arrays.asList(new Interval(10, true, null, true));
        quantity = IntervalHelper.quantityFromCoarsestPrecisionOfBoundaries(intervals);
        assertEquals(1, quantity.getValue().intValue());
        assertEquals(0, quantity.getValue().scale());
        assertTrue(Quantity.isDefaultUnit(quantity.getUnit()));
    }

    @Test
    void isQuantityCompatibleWithBoundaries() {
        var quantityWithDefaultUnit =
                new Quantity().withValue(new BigDecimal("1.123")).withDefaultUnit();
        var gramsQuantity = new Quantity().withValue(new BigDecimal("1.123")).withUnit("g");
        var monthsQuantity = new Quantity().withValue(new BigDecimal("10")).withUnit("month");

        var intervals = Arrays.asList(new Interval(10, true, null, false));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));

        intervals = Arrays.asList(new Interval(1L, true, null, true));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));

        intervals = Arrays.asList(null, new Interval(new BigDecimal("1.1"), true, null, true));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));

        intervals = Arrays.asList(
                new Interval(new Quantity().withValue(new BigDecimal("1.1")).withUnit("g"), true, null, true));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));

        intervals = Arrays.asList(new Interval(null, false, new Date("2025-12"), true));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(monthsQuantity, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));
        assertFalse(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));

        intervals = Arrays.asList((Interval) null);
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(quantityWithDefaultUnit, intervals));
        assertTrue(IntervalHelper.isQuantityCompatibleWithBoundaries(gramsQuantity, intervals));
    }
}
