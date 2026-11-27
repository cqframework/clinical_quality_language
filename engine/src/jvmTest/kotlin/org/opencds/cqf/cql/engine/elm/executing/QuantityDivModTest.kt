package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Value

/** Truncated Divide (div) and Modulo (mod) on Quantities with different units (FHIR-59659). */
internal class QuantityDivModTest : CqlTestBase() {
    private fun q(value: String, unit: String) =
        Quantity().withValue(BigDecimal(value)).withUnit(unit)

    private fun state() = State(environment!!)

    private fun assertQuantity(expectedValue: String, expectedUnit: String, actual: Value?) {
        assertIs<Quantity>(actual)
        assertEquals(expectedUnit, actual.unit)
        assertEquals(
            0,
            actual.value!!.compareTo(BigDecimal(expectedValue)),
            "value ${actual.value}",
        )
    }

    @Test
    fun divConvertsToACommonUnitAndIsUnitless() {
        assertQuantity("2", "1", TruncatedDivideEvaluator.div(q("250", "cm"), q("1", "m"), state()))
        assertQuantity("0", "1", TruncatedDivideEvaluator.div(q("1", "m"), q("250", "cm"), state()))
        assertQuantity(
            "3500",
            "1",
            TruncatedDivideEvaluator.div(q("7", "kg"), q("2", "g"), state()),
        )
        assertQuantity(
            "-2",
            "1",
            TruncatedDivideEvaluator.div(q("-250", "cm"), q("1", "m"), state()),
        )
        assertQuantity("2", "1", TruncatedDivideEvaluator.div(q("10", "g"), q("5", "g"), state()))
    }

    @Test
    fun divByAUnitlessQuantityKeepsTheLeftUnit() {
        assertQuantity("3", "g", TruncatedDivideEvaluator.div(q("10", "g"), q("3", "1"), state()))
    }

    @Test
    fun divOfDifferentDimensionsTruncatesTheQuotient() {
        // Not convertible to a common unit, so divided as in Divide: 7 g / 2 m = 3.5 g/m.
        val result = TruncatedDivideEvaluator.div(q("7", "g"), q("2", "m"), state())
        assertIs<Quantity>(result)
        assertEquals(0, result.value!!.compareTo(BigDecimal("3")), "value ${result.value}")
    }

    @Test
    fun modIsInTheMoreGranularUnit() {
        assertQuantity("10", "cm", ModuloEvaluator.modulo(q("1", "m"), q("30", "cm"), state()))
        assertQuantity("100", "g", ModuloEvaluator.modulo(q("1", "kg"), q("300", "g"), state()))
        // The more granular unit is chosen whichever side it's on.
        assertQuantity("300", "g", ModuloEvaluator.modulo(q("300", "g"), q("1", "kg"), state()))
    }

    @Test
    fun modByAUnitlessQuantityKeepsTheLeftUnit() {
        assertQuantity("1", "g", ModuloEvaluator.modulo(q("10", "g"), q("3", "1"), state()))
    }

    @Test
    fun modOfDifferentDimensionsIsNull() {
        assertNull(ModuloEvaluator.modulo(q("7", "g"), q("2", "m"), state()))
    }

    @Test
    fun calendarDurationsConvertThroughUcum() {
        // CQL keywords aren't UCUM units, but 1 week = 7 days.
        assertQuantity(
            "2",
            "1",
            TruncatedDivideEvaluator.div(q("14", "days"), q("1", "week"), state()),
        )
        assertQuantity(
            "3",
            "days",
            ModuloEvaluator.modulo(q("10", "days"), q("1", "week"), state()),
        )
        assertQuantity("1", "d", ModuloEvaluator.modulo(q("10", "days"), q("3", "d"), state()))
        assertQuantity(
            "12",
            "1",
            TruncatedDivideEvaluator.div(q("1", "year"), q("1", "month"), state()),
        )
    }

    @Test
    fun yearsAndMonthsDontConvertToDefiniteDurations() {
        assertNull(ModuloEvaluator.modulo(q("1", "year"), q("30", "days"), state()))
        assertNull(TruncatedDivideEvaluator.div(q("1", "month"), q("1", "day"), state()))
    }

    @Test
    fun divOfUnitsUcumCantCombineIsNull() {
        // Divide throws for a calendar keyword it can't hand to UCUM; div gives null instead.
        assertNull(TruncatedDivideEvaluator.div(q("14", "days"), q("1", "m"), state()))
    }

    @Test
    fun divResultOutsideTheDecimalRangeIsNull() {
        assertNull(
            TruncatedDivideEvaluator.div(
                q("99999999999999999999.0", "kg"),
                q("0.001", "g"),
                state(),
            )
        )
    }

    @Test
    fun divResultHasNoDecimalPlaces() {
        // 1 'a' is 365.25 'd'; unit conversion leaves decimal places on the quotient.
        val result = TruncatedDivideEvaluator.div(q("1", "a"), q("1", "d"), state())
        assertQuantity("365", "1", result)
        assertEquals(0, (result as Quantity).value!!.scale())
        val fallback = TruncatedDivideEvaluator.div(q("7.0", "g"), q("2.0", "m"), state())
        assertEquals(0, (fallback as Quantity).value!!.scale())
    }

    @Test
    fun divAndModByZeroAreNull() {
        assertNull(TruncatedDivideEvaluator.div(q("10", "cm"), q("0", "m"), state()))
        assertNull(ModuloEvaluator.modulo(q("10", "cm"), q("0", "m"), state()))
    }
}
