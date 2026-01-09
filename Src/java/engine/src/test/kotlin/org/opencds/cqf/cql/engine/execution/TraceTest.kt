package org.opencds.cqf.cql.engine.execution

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class TraceTest : CqlTestBase() {

    @Test
    fun traceOutput() {
        engine.state.engineOptions.add(CqlEngine.Options.EnableTracing)
        val result = engine.evaluate { library("TraceTest") }.onlyResultOrThrow
        assertEquals(
            """
                "TraceTest.expr1" = 2
                "TraceTest.expr2" = 19
                  "TraceTest.func2"(b = 5) = 19
                    "TraceTest.func1"(a = 7) = 8
                    "TraceTest.func1"(a = 8) = 9
                    "TraceTest.expr1" = 2

            """
                .trimIndent(),
            result.trace.toString(),
        )
    }
}
