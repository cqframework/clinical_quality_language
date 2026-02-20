package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.StaticFunction
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
                MyMath::class.java.declaredMethods.map {
                    StaticFunction(
                        it.name,
                        { arguments -> it.invoke(it.declaringClass, *arguments!!.toTypedArray()) },
                    )
                }
            ),
        )

        val results = engine.evaluate { library(identifier) }.onlyResultOrThrow
        var value = results["CallMyPlus"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(10))

        value = results["CallMyMinus"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2))
    }
}
