package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider
import org.opencds.cqf.cql.engine.execution.external.MyMath2

internal class CqlExternalFunctionsTest2 : CqlTestBase() {
    @Test
    fun externalFunctions() {
        val identifier = toElmIdentifier("CqlExternalFunctionsTest2")

        val engine = engine
        engine.environment.registerExternalFunctionProvider(
            identifier,
            SystemExternalFunctionProvider(listOf(*MyMath2::class.java.getDeclaredMethods())),
        )

        val results = engine.evaluate { library(identifier) }.onlyResultOrThrow
        var value = results.forExpression("CallMyTimes")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(54))

        value = results.forExpression("CallMyDividedBy")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(6))
    }
}
