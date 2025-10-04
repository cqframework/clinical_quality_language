package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class SignatureOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate(toElmIdentifier("SignatureOutputTests"))
        var value = results.forExpression("TestIntegerOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1")))

        value = results.forExpression("TestDecimalOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1.0")))

        value = results.forExpression("TestMultipleOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results.forExpression("TestIntegerMultipleOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("TestDecimalMultipleOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results.forExpression("TestIntegerAndDecimalMultipleOverload")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))
    }
}
