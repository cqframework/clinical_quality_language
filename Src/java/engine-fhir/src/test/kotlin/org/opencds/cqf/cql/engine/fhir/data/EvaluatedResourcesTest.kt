package org.opencds.cqf.cql.engine.fhir.data

import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine

internal class EvaluatedResourcesTest : FhirExecutionTestBase() {
    @Test
    fun withCache() {
        val engine = getCqlEngineForFhir(true)

        var results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Union"))

        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Encounter"))
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Encounter",
            listOf(EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Condition"))
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Condition",
            listOf(EvaluatedResourceTestUtils.CONDITION),
        )
    }

    @Test
    fun withoutCache() {
        val engine = getCqlEngineForFhir(false)

        var results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Union"))

        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Encounter"))
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Encounter",
            listOf(EvaluatedResourceTestUtils.ENCOUNTER),
        )

        results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Condition"))
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Condition",
            listOf(EvaluatedResourceTestUtils.CONDITION),
        )

        results = engine.evaluate(library!!.identifier, mutableSetOf<String?>("Union"))
        EvaluatedResourceTestUtils.assertEvaluationResult(
            results,
            "Union",
            listOf(EvaluatedResourceTestUtils.CONDITION, EvaluatedResourceTestUtils.ENCOUNTER),
        )
    }

    private fun getCqlEngineForFhir(expressionCaching: Boolean): CqlEngine {
        engine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, EvaluatedResourceTestUtils.RETRIEVE_PROVIDER),
        )
        engine.cache.setExpressionCaching(expressionCaching)
        return engine
    }
}
