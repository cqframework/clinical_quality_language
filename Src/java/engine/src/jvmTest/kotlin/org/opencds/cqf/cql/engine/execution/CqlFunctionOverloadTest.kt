package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlFunctionOverloadTest : CqlTestBase() {
    @Test
    fun function_overloads() {
        val results = engine.evaluate { library("FunctionOverloadTest") }.onlyResultOrThrow
        var value = results["TestAnyFunctionWithInteger"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["TestAnyFunctionWithString"]!!.value
        assertEquals("joe".toCqlString(), value)

        value = results["TestAnyFunctionWithDecimal"]!!.value
        assertEquals(BigDecimal("12.3").toCqlDecimal(), value)

        value = results["TestAnyFunctionWithNoArgs"]!!.value
        assertEquals("any".toCqlString(), value)
    }
}
