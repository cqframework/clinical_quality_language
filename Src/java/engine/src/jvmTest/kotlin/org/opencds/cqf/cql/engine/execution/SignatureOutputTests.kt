package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class SignatureOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate { library("SignatureOutputTests") }.onlyResultOrThrow
        var value = results["TestIntegerOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1")))

        value = results["TestDecimalOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1.0")))

        value = results["TestMultipleOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results["TestIntegerMultipleOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["TestDecimalMultipleOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results["TestIntegerAndDecimalMultipleOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))
    }
}
