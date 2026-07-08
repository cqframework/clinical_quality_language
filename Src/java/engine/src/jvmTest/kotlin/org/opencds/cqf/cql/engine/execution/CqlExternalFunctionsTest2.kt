package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.data.StaticFunction
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider
import org.opencds.cqf.cql.engine.execution.external.MyMath2
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class CqlExternalFunctionsTest2 : CqlTestBase() {
    @Test
    fun externalFunctions() {
        val identifier = toElmIdentifier("CqlExternalFunctionsTest2")

        val engine = engine
        engine.environment.registerExternalFunctionProvider(
            identifier,
            SystemExternalFunctionProvider(
                MyMath2::class.java.declaredMethods.map {
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
        var value = results["CallMyTimes"]!!.value
        assertEquals(54.toCqlInteger(), value)

        value = results["CallMyDividedBy"]!!.value
        assertEquals(6.toCqlInteger(), value)
    }
}
