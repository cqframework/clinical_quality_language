package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.hl7.cql.ast.ProblemSeverity

class BuilderSmokeTest {

    private val frontend = CqlFrontend()

    private fun build(text: String): LibraryResult =
        frontend.buildLibrary(text.trimIndent(), sourceId = "test.cql")

    private fun analyze(result: LibraryResult): LibraryAnalysisResult =
        frontend.analyzeLibrary(result.library)

    private fun assertNoProblems(result: LibraryResult) {
        assertTrue(
            result.problems.isEmpty(),
            "Expected no syntax problems but found: ${result.problems.joinToString { it.message }}",
        )
    }

    @Test
    fun parsesMinimalLibraryWithCodesystem() {
        val result =
            build(
                """
                library Minimal version '1.0.0'
                using SimpleModel version '1.0'

                codesystem "SNOMED": 'http://snomed.info/sct'

                define "Always True": true
                """
            )

        assertNoProblems(result)
        assertEquals("Minimal", result.library.name?.simpleName)
        assertEquals(2, result.library.definitions.size)
    }

    @Test
    fun parsesValueSetAndConceptDefinitions() {
        val result =
            build(
                """
                library Terminology version '1.0'

                codesystem "CVX": 'http://hl7.org/fhir/sid/cvx'
                valueset "Influenza Vaccines": 'urn:oid:1.2.3.4' codesystems { "CVX" }
                concept "Quadrivalent Vaccines": { "CVX"."140", "CVX"."141" }

                define "Is Vaccine": "CVX"."140" = "CVX"."140"
                """
            )

        assertNoProblems(result)
        assertEquals(3, result.library.definitions.size)
    }

    @Test
    fun parsesParameterWithDefaultAndFunctionDefinition() {
        val result =
            build(
                """
                library SharedLogic version '2.1'

                parameter "Patient Id" String default '12345'

                define function "Are Equal"(left String, right String):
                  left = right
                """
            )

        assertNoProblems(result)
        assertEquals(2, result.library.definitions.size + result.library.statements.size)
    }

    @Test
    fun parsesPropertyAccessExpression() {
        val result =
            build(
                """
                library Properties version '1.0'

                define "Patient Name": Patient.name
                """
            )

        assertNoProblems(result)
        assertEquals(1, result.library.statements.size)
    }

    @Test
    fun parsesBooleanComparisonExpression() {
        val result =
            build(
                """
                library Booleans version '1.0'

                define "Null Check": true is not null
                define "Equality": 'abc' = 'abc'
                define "Inequality": 5 < 10
                """
            )

        assertNoProblems(result)
        assertEquals(3, result.library.statements.size)
    }

    @Test
    fun parsesQueryAndRetrieveConstructs() {
        val queryLibrary =
            """
            library QueryExamples version '1.0'
            using QUICK version '1.0'

            codesystem "SNOMED": 'http://snomed.info/sct'
            valueset "Influenza Vaccines": 'urn:oid:1.2.3.4' codesystems { "SNOMED" }

            define "SimpleRetrieve":
              [Observation: category in "Influenza Vaccines"]

            define "RetrieveWithCode":
              [Observation: category in Code '140' from "SNOMED"]

            define "ComprehensiveQuery":
              from [Observation: category in "Influenza Vaccines"] O
                let Value: O.value
                with [Encounter] E such that true
                where Value != null
                return distinct Value
                sort by Value descending

            define "QueryWithWithout":
              from [Observation: category in "Influenza Vaccines"] O
                without [Observation] O2 such that true
                return O
            """

        val result = build(queryLibrary)
        assertNoProblems(result)
        assertEquals(4, result.library.statements.size)
    }

    @Test
    fun warnsOnIdentifierShadowing() {
        val buildResult =
            build(
                """
                library Shadowing version '1.0'

                define function \"Foo\"(value Integer):
                  from [Observation] O
                    let value: 1
                    return value
                """
            )

        val analysis = analyze(buildResult)

        assertTrue(
            analysis.problems.any { it.severity == ProblemSeverity.WARNING && "value" in it.message },
            "Expected a shadowing warning but found: ${analysis.problems}",
        )
    }

    @Test
    fun analysisProducesAstNodeIds() {
        val buildResult =
            build(
                """
                library Metadata version '1.0'

                define "One": 1
                """
            )

        val analysis = analyze(buildResult)
        val ids = analysis.metadata.ids

        val statement = buildResult.library.statements.first() as ExpressionDefinition
        val statementId = ids.idFor(statement)
        val expressionId = ids.idFor(statement.expression)

        assertTrue(statementId.value > 0)
        assertTrue(expressionId.value > 0)
        assertEquals(
            null,
            analysis.metadata.typeTable.get(statement.expression),
            "Stub type inference should not assign types yet.",
        )
    }
}
