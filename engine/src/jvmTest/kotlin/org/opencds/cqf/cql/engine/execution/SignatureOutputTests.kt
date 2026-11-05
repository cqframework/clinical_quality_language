package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class SignatureOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate { library("SignatureOutputTests") }.onlyResultOrThrow
        var value = results["TestIntegerOverload"]!!.value
        assertEquals(BigDecimal("1").toCqlDecimal(), value)

        value = results["TestDecimalOverload"]!!.value
        assertEquals(BigDecimal("1.0").toCqlDecimal(), value)

        value = results["TestMultipleOverload"]!!.value
        assertEquals(5.toCqlInteger(), value)

        value = results["TestIntegerMultipleOverload"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["TestDecimalMultipleOverload"]!!.value
        assertEquals(BigDecimal("2.0").toCqlDecimal(), value)

        value = results["TestIntegerAndDecimalMultipleOverload"]!!.value
        assertEquals(Integer.ONE, value)
    }
}
