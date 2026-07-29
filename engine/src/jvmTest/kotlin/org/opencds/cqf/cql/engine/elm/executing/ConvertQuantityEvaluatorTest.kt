package org.opencds.cqf.cql.engine.elm.executing

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class ConvertQuantityEvaluatorTest : CqlTestBase() {
    @Test
    fun convertQuantityWithCalendarUnitSource() {
        // Regression for #1682: FHIRHelpers.ToQuantity yields calendar-keyword units (a FHIR 'd'
        // becomes "day"), which UCUM cannot parse. Converting such a quantity must still succeed
        // instead of silently returning null.
        val ucumService = environment!!.libraryManager!!.ucumService
        val source = Quantity().withValue(BigDecimal("30")).withUnit("day")

        val result =
            ConvertQuantityEvaluator.convertQuantity(source, "d".toCqlString(), ucumService)

        assertNotNull(result)
        assertEquals("d", result!!.unit)
        assertEquals(0, result.value!!.compareTo(BigDecimal("30")))
    }
}
