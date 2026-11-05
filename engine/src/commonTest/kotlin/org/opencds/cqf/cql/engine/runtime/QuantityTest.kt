package org.opencds.cqf.cql.engine.runtime

import kotlin.test.Test
import kotlin.test.assertEquals

internal class QuantityTest {
    @Test
    fun toUcumUnitMapsCalendarKeywordsToUcumCodes() {
        // Every CQL calendar-duration keyword (singular and plural) maps to its UCUM code so the
        // value can be handed to the UCUM service (#1682).
        assertEquals("a", Quantity.toUcumUnit("year"))
        assertEquals("a", Quantity.toUcumUnit("years"))
        assertEquals("mo", Quantity.toUcumUnit("month"))
        assertEquals("mo", Quantity.toUcumUnit("months"))
        assertEquals("wk", Quantity.toUcumUnit("week"))
        assertEquals("wk", Quantity.toUcumUnit("weeks"))
        assertEquals("d", Quantity.toUcumUnit("day"))
        assertEquals("d", Quantity.toUcumUnit("days"))
        assertEquals("h", Quantity.toUcumUnit("hour"))
        assertEquals("h", Quantity.toUcumUnit("hours"))
        assertEquals("min", Quantity.toUcumUnit("minute"))
        assertEquals("min", Quantity.toUcumUnit("minutes"))
        assertEquals("s", Quantity.toUcumUnit("second"))
        assertEquals("s", Quantity.toUcumUnit("seconds"))
        assertEquals("ms", Quantity.toUcumUnit("millisecond"))
        assertEquals("ms", Quantity.toUcumUnit("milliseconds"))
    }

    @Test
    fun toUcumUnitLeavesNonCalendarUnitsUnchanged() {
        // Already-valid UCUM codes (including UCUM's own time atoms and the annotation/default
        // units) must pass through untouched.
        assertEquals("mg", Quantity.toUcumUnit("mg"))
        assertEquals("g", Quantity.toUcumUnit("g"))
        assertEquals("mg/dL", Quantity.toUcumUnit("mg/dL"))
        assertEquals("d", Quantity.toUcumUnit("d"))
        assertEquals("wk", Quantity.toUcumUnit("wk"))
        assertEquals("1", Quantity.toUcumUnit("1"))
        assertEquals("", Quantity.toUcumUnit(""))
        assertEquals("{score}", Quantity.toUcumUnit("{score}"))
    }
}
