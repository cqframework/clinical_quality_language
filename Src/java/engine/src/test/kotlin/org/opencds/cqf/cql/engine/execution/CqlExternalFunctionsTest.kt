package org.opencds.cqf.cql.engine.execution

import java.lang.reflect.Method
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider
import org.opencds.cqf.cql.engine.execution.external.MyMath

internal class CqlExternalFunctionsTest : CqlTestBase() {
    @Test
    fun externalFunctions() {
        val identifier = toElmIdentifier("CqlExternalFunctionsTest")

        val engine = engine
        engine.state.environment.registerExternalFunctionProvider(
            identifier,
            SystemExternalFunctionProvider(
                listOf<Method?>(*MyMath::class.java.getDeclaredMethods())
            ),
        )

        val results = engine.evaluate(identifier)
        var value = results.forExpression("CallMyPlus").value()
        MatcherAssert.assertThat(value, Matchers.`is`(10))

        value = results.forExpression("CallMyMinus").value()
        MatcherAssert.assertThat(value, Matchers.`is`(-2))
    }
}
