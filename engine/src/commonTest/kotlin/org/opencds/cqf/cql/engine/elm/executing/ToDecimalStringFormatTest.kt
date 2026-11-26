package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlString

/**
 * ToDecimal accepts strings in the format (+|-)?#0(.0#)?, and ConvertsToDecimal is true exactly
 * when ToDecimal returns a value (https://cql.hl7.org/09-b-cqlreference.html#todecimal).
 */
class ToDecimalStringFormatTest {
    private fun toDecimal(value: String) = ToDecimalEvaluator.toDecimal(value.toCqlString())

    private fun convertsToDecimal(value: String) =
        ConvertsToDecimalEvaluator.convertsToDecimal(value.toCqlString())

    @Test
    fun validFormats() {
        for ((input, expected) in
            listOf(
                "1" to "1",
                "+25.5" to "25.5",
                "-0.1" to "-0.1",
                "007.50" to "7.50",
                "0.12345678" to "0.12345678",
            )) {
            assertEquals(BigDecimal(expected).toCqlDecimal(), toDecimal(input), input)
            assertEquals(Boolean.TRUE, convertsToDecimal(input), input)
        }
    }

    @Test
    fun stringsOutsideTheFormatAreNotConverted() {
        for (input in
            listOf(
                "1.",
                "1e5",
                "1E-2",
                "1.5f",
                "1.5d",
                "NaN",
                "Infinity",
                ".5",
                "+.1",
                "+-0.1",
                " 1.5",
                "1.5 ",
                "1,000",
                "١٢",
                "",
            )) {
            assertNull(toDecimal(input), input)
            assertEquals(Boolean.FALSE, convertsToDecimal(input), input)
        }
    }

    @Test
    fun valuesOutsideTheDecimalRangeAreNotConverted() {
        assertNull(toDecimal("444444444444444444444444444444"))
        assertEquals(Boolean.FALSE, convertsToDecimal("444444444444444444444444444444"))
    }
}
