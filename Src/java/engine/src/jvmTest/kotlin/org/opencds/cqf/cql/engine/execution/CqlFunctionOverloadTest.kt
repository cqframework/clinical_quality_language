package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class CqlFunctionOverloadTest : CqlTestBase() {
    @Test
    fun function_overloads() {
        val results = engine.evaluate { library("FunctionOverloadTest") }.onlyResultOrThrow
        var value = results["TestAnyFunctionWithInteger"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["TestAnyFunctionWithString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("joe"))

        value = results["TestAnyFunctionWithDecimal"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("12.3")))

        value = results["TestAnyFunctionWithNoArgs"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("any"))
    }
}
