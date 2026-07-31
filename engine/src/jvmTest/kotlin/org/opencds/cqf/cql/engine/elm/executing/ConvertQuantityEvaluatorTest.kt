package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class ConvertQuantityEvaluatorTest : CqlTestBase() {
    private val ucumService = libraryManager.ucumService

    private fun convert(value: kotlin.String, unit: kotlin.String, target: kotlin.String) =
        ConvertQuantityEvaluator.convertQuantity(
            Quantity().withValue(BigDecimal(value)).withUnit(unit),
            target.toCqlString(),
            ucumService,
        )

    @Test
    fun convertQuantityWithCalendarUnitSource() {
        // Regression for #1682: FHIRHelpers.ToQuantity yields calendar-keyword units (a FHIR 'd'
        // becomes "day"), which UCUM cannot parse. Converting such a quantity must still succeed
        // instead of silently returning null.
        val result = convert("30", "day", "d")

        assertNotNull(result)
        assertEquals("d", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("30")))
    }

    @Test
    fun convertQuantityCalendarSourceActuallyConverts() {
        // A value-changing conversion proves the calendar keyword is normalized *and* the UCUM
        // conversion runs (not merely relabeled): 1 week = 7 days.
        val result = convert("1", "week", "d")

        assertNotNull(result)
        assertEquals("d", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("7")))
    }

    @Test
    fun convertQuantityCalendarSourceAndCalendarTarget() {
        // The reported shape, `convert D.daysSupply to days`: both source and target are calendar
        // keywords. The result keeps the requested target keyword as its unit.
        val result = convert("30", "day", "days")

        assertNotNull(result)
        assertEquals("days", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("30")))
    }

    @Test
    fun convertQuantityCalendarTargetActuallyConverts() {
        // Calendar keyword as the target, with a value-changing conversion: 2 days = 48 hours.
        val result = convert("2", "day", "hour")

        assertNotNull(result)
        assertEquals("hour", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("48")))
    }

    @Test
    fun convertQuantityPlainUcumUnitsUnaffected() {
        // The normalization must not disturb ordinary UCUM conversions: 5 mg = 0.005 g.
        val result = convert("5", "mg", "g")

        assertNotNull(result)
        assertEquals("g", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("0.005")))
    }

    @Test
    fun convertQuantityIncompatibleUnitsReturnNull() {
        // Mass cannot be converted to a duration; UCUM throws and the operator yields null.
        assertNull(convert("5", "mg", "day"))
    }

    @Test
    fun convertQuantityNullArgumentsReturnNull() {
        assertNull(ConvertQuantityEvaluator.convertQuantity(null, "g".toCqlString(), ucumService))
        assertNull(
            ConvertQuantityEvaluator.convertQuantity(
                Quantity().withValue(BigDecimal("5")).withUnit("mg"),
                null,
                ucumService,
            )
        )
    }

    @Test
    fun convertQuantityNullUcumServiceReturnsNull() {
        assertNull(
            ConvertQuantityEvaluator.convertQuantity(
                Quantity().withValue(BigDecimal("5")).withUnit("mg"),
                "g".toCqlString(),
                null,
            )
        )
    }
}
