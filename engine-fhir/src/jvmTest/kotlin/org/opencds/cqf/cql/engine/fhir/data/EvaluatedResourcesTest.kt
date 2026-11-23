package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine

internal class EvaluatedResourcesTest : FhirExecutionTestBase() {
    @Test
    fun withCache() {
        val engine = getCqlEngineForFhir(true)

        var results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Union") } }
                .onlyResultOrThrow

        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Encounter") } }
                .onlyResultOrThrow
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Encounter",
            listOf(EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Condition") } }
                .onlyResultOrThrow
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Condition",
            listOf(EvaluatedResourceTestUtils.CONDITION),
        )
    }

    @Test
    fun withoutCache() {
        val engine = getCqlEngineForFhir(false)

        var results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Union") } }
                .onlyResultOrThrow

        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Encounter") } }
                .onlyResultOrThrow
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Encounter",
            listOf(EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Condition") } }
                .onlyResultOrThrow
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Condition",
            listOf(EvaluatedResourceTestUtils.CONDITION),
        )

        results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Union") } }
                .onlyResultOrThrow
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )
    }

    @Test
    fun disabledCollectionLeavesEvaluatedResourcesEmpty() {
        val engine = getCqlEngineForFhir(true)
        engine.state.engineOptions.add(CqlEngine.Options.DisableEvaluatedResourcesCollection)

        val results =
            engine
                .evaluate { this.library(library!!.identifier!!) { expressions("Union") } }
                .onlyResultOrThrow

        val result = results["Union"]!!
        assertNotNull(result.value)
        assertTrue(result.evaluatedResources!!.isEmpty())
    }

    private fun getCqlEngineForFhir(expressionCaching: Boolean): CqlEngine {
        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(
                r4ModelResolver!!,
                EvaluatedResourceTestUtils.getRetrieveProvider(r4ModelResolver!!),
            ),
        )
        engine.cache.setExpressionCaching(expressionCaching)
        return engine
    }
}
