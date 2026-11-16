package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity

/**
 * Quantities in different units can only be compared with the UCUM service of a state, so the order
 * of their boundaries is checked by the [Interval] constructor that takes one.
 */
internal class IntervalOrderCheckTest : CqlTestBase() {
    @Test
    fun quantityBoundariesInDifferentUnitsAreCheckedWithTheState() {
        // 1 'g' is 1000 'mg'
        assertFailsWith<InvalidInterval> {
            Interval(
                Quantity(BigDecimal("1"), "g"),
                true,
                Quantity(BigDecimal("500"), "mg"),
                true,
                state = engine.state,
            )
        }

        val interval =
            Interval(
                Quantity(BigDecimal("1"), "g"),
                true,
                Quantity(BigDecimal("2000"), "mg"),
                true,
                state = engine.state,
            )
        assertEquals("mg", (interval.high as Quantity).unit)
    }

    @Test
    fun quantityBoundariesInTheSameUnitAreCheckedWithTheState() {
        assertFailsWith<InvalidInterval> {
            Interval(
                Quantity(BigDecimal("5"), "mg"),
                true,
                Quantity(BigDecimal("2"), "mg"),
                true,
                state = engine.state,
            )
        }
    }
}
