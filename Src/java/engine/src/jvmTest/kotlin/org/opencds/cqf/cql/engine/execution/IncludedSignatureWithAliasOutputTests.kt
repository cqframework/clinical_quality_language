package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class IncludedSignatureWithAliasOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results =
            engine.evaluate { library("IncludedSignatureWithAliasOutputTests") }.onlyResultOrThrow

        var value = results["TestOverload"]!!.value
        assertEquals(5.toCqlInteger(), value)

        value = results["TestOverloadOneInt"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["TestOverloadOneDecimal"]!!.value
        assertEquals(BigDecimal("2.0").toCqlDecimal(), value)

        value = results["TestOverloadTwoInts"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["TestOverloadTwoDecimals"]!!.value
        assertEquals(BigDecimal("2.0").toCqlDecimal(), value)

        value = results["TestOverloadOneIntOneDecimal"]!!.value
        assertEquals(BigDecimal("2.0").toCqlDecimal(), value)

        value = results["TestOverloadOneIntTwoDecimal"]!!.value
        assertEquals(1.toCqlInteger(), value)
    }
}
