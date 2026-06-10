package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class IncludedParameterTest : CqlTestBase() {
    @Test
    fun gets_global_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params = mutableMapOf("Measurement Period" to 1.toCqlInteger())

        val result =
            engine
                .evaluate {
                    this.library(library) { expressions(expressions) }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a global should affect all expressions
        assertEquals(1.toCqlInteger(), result["Included Parameter"]!!.value)
        assertEquals(1.toCqlInteger(), result["Local Parameter"]!!.value)
    }

    @Test
    fun local_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params = mutableMapOf("IncludedParameterTest.Measurement Period" to 1.toCqlInteger())

        val result =
            engine
                .evaluate {
                    this.library(library) { expressions(expressions) }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a local should only impact the local value
        assertNull(result["Included Parameter"]!!.value)
        assertEquals(1.toCqlInteger(), result["Local Parameter"]!!.value)
    }

    @Test
    fun include_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params =
            mutableMapOf("IncludedParameterTestCommon.Measurement Period" to 1.toCqlInteger())

        val result: EvaluationResult =
            engine
                .evaluate {
                    this.library(library) { expressions(expressions) }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a local should only impact the local value
        assertNull(result["Local Parameter"]!!.value)
        assertEquals(1.toCqlInteger(), result["Included Parameter"]!!.value)
    }

    @Test
    fun local_override_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params =
            mutableMapOf(
                "Measurement Period" to 2.toCqlInteger(),
                "IncludedParameterTestCommon.Measurement Period" to 1.toCqlInteger(),
            )

        val result =
            engine
                .evaluate {
                    this.library(library) { expressions(expressions) }
                    parameters = params
                }
                .onlyResultOrThrow
        // If a library-specific parameter is not specified, the global
        // value should be used
        assertEquals(2.toCqlInteger(), result["Local Parameter"]!!.value)
        assertEquals(1.toCqlInteger(), result["Included Parameter"]!!.value)
    }

    companion object {
        private val library = VersionedIdentifier().withId("IncludedParameterTest")
    }
}
