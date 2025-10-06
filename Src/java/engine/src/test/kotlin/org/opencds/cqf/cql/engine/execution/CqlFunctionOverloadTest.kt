package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class CqlFunctionOverloadTest : CqlTestBase() {
    @Test
    fun function_overloads() {
        val results = engine.evaluate(toElmIdentifier("FunctionOverloadTest"))
        var value = results.forExpression("TestAnyFunctionWithInteger")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("TestAnyFunctionWithString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("joe"))

        value = results.forExpression("TestAnyFunctionWithDecimal")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("12.3")))

        value = results.forExpression("TestAnyFunctionWithNoArgs")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("any"))
    }
}
