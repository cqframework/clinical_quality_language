package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.data.StaticFunction
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider
import org.opencds.cqf.cql.engine.execution.external.MyMath
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

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
                        { arguments ->
                            it.invoke(it.declaringClass, *arguments!!.toTypedArray()) as Value?
                        },
                    )
                }
            ),
        )

        val results = engine.evaluate { library(identifier) }.onlyResultOrThrow
        var value = results["CallMyPlus"]!!.value
        assertEquals(10.toCqlInteger(), value)

        value = results["CallMyMinus"]!!.value
        assertEquals((-2).toCqlInteger(), value)
    }
}
