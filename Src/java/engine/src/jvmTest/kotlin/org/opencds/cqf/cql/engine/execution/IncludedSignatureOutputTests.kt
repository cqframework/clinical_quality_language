package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class IncludedSignatureOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate { library("IncludedSignatureOutputTests") }.onlyResultOrThrow

        var value = results["TestOverload"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results["TestOverloadOneInt"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["TestOverloadOneDecimal"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results["TestOverloadTwoInts"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["TestOverloadTwoDecimals"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results["TestOverloadOneIntOneDecimal"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results["TestOverloadOneIntTwoDecimal"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))
    }
}
