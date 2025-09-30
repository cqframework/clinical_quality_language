package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class IncludedSignatureWithAliasOutputTests : CqlTestBase() {
    @Test
    fun evaluate() {
        val results = engine.evaluate(toElmIdentifier("IncludedSignatureWithAliasOutputTests"))

        var value = results.forExpression("TestOverload").value()
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results.forExpression("TestOverloadOneInt").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("TestOverloadOneDecimal").value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results.forExpression("TestOverloadTwoInts").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("TestOverloadTwoDecimals").value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results.forExpression("TestOverloadOneIntOneDecimal").value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("2.0")))

        value = results.forExpression("TestOverloadOneIntTwoDecimal").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))
    }
}
