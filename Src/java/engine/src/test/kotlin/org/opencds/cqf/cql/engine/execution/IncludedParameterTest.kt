package org.opencds.cqf.cql.engine.execution

import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class IncludedParameterTest : CqlTestBase() {
    @Test
    fun gets_global_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params = mutableMapOf("Measurement Period" to 1)

        val result =
            engine
                .evaluate {
                    this.library(library)
                    for (expressionName in expressions) {
                        expression(expressionName)
                    }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a global should affect all expressions
        Assertions.assertEquals(1, result.forExpression("Included Parameter")!!.value)
        Assertions.assertEquals(1, result.forExpression("Local Parameter")!!.value)
    }

    @Test
    fun local_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params = mutableMapOf("IncludedParameterTest.Measurement Period" to 1)

        val result =
            engine
                .evaluate {
                    this.library(library)
                    for (expressionName in expressions) {
                        expression(expressionName)
                    }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a local should only impact the local value
        Assertions.assertNull(result.forExpression("Included Parameter")!!.value)
        Assertions.assertEquals(1, result.forExpression("Local Parameter")!!.value)
    }

    @Test
    fun include_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params = mutableMapOf("IncludedParameterTestCommon.Measurement Period" to 1)

        val result: EvaluationResult =
            engine
                .evaluate {
                    this.library(library)
                    for (expressionName in expressions) {
                        expression(expressionName)
                    }
                    parameters = params
                }
                .onlyResultOrThrow
        // Parameter added as a local should only impact the local value
        Assertions.assertNull(result.forExpression("Local Parameter")!!.value)
        Assertions.assertEquals(1, result.forExpression("Included Parameter")!!.value)
    }

    @Test
    fun local_override_param_value() {
        val expressions = mutableSetOf("Included Parameter", "Local Parameter")

        val params =
            mutableMapOf<String, Any?>(
                "Measurement Period" to 2,
                "IncludedParameterTestCommon.Measurement Period" to 1,
            )

        val result =
            engine
                .evaluate {
                    this.library(library)
                    for (expressionName in expressions) {
                        expression(expressionName)
                    }
                    parameters = params
                }
                .onlyResultOrThrow
        // If a library-specific parameter is not specified, the global
        // value should be used
        Assertions.assertEquals(2, result.forExpression("Local Parameter")!!.value)
        Assertions.assertEquals(1, result.forExpression("Included Parameter")!!.value)
    }

    companion object {
        private val library = VersionedIdentifier().withId("IncludedParameterTest")
    }
}
