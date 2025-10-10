package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CqlAstBuilderSmokeTest {

    private fun parse(text: String): AstResult =
        CqlAstBuilder("test.cql").parseLibrary(text.trimIndent())

    private fun parseExpressions(expressions: List<String>, extraHeader: String = ""): AstResult {
        val header =
            """
            library Comprehensive version '1.0'
            using QUICK version '1.0'

            codesystem "SNOMED": 'http://snomed.info/sct'
            valueset "Influenza Vaccines": 'urn:oid:1.2.3.4' codesystems { "SNOMED" }
            code "FluCode": '140' from "SNOMED"
            concept "FluConcept": { "SNOMED"."140" }

            $extraHeader
            """
                .trimIndent()

        val body =
            expressions
                .mapIndexed { index, expr -> """define "Expr$index": $expr""" }
                .joinToString(separator = "\n")

        return parse(
            """
            $header

            $body
            """,
        )
    }

    private fun assertNoProblems(result: AstResult) {
        assertTrue(
            result.problems.isEmpty(),
            "Expected no syntax problems but found: ${result.problems.joinToString { it.message }}",
        )
    }

    @Test
    fun parsesMinimalLibraryWithCodesystem() {
        val result =
            parse(
                """
                library Minimal version '1.0.0'
                using SimpleModel version '1.0'

                codesystem "SNOMED": 'http://snomed.info/sct'

                define "Always True": true
                """,
            )

        assertNoProblems(result)
        assertEquals("Minimal", result.library.name?.simpleName)
        assertEquals(2, result.library.definitions.size)
    }

    @Test
    fun parsesValueSetAndConceptDefinitions() {
        val result =
            parse(
                """
                library Terminology version '1.0'

                codesystem "CVX": 'http://hl7.org/fhir/sid/cvx'
                valueset "Influenza Vaccines": 'urn:oid:1.2.3.4' codesystems { "CVX" }
                concept "Quadrivalent Vaccines": { "CVX"."140", "CVX"."141" }

                define "Is Vaccine": "CVX"."140" = "CVX"."140"
                """,
            )

        assertNoProblems(result)
        assertEquals(3, result.library.definitions.size)
    }

    @Test
    fun parsesParameterWithDefaultAndFunctionDefinition() {
        val result =
            parse(
                """
                library SharedLogic version '2.1'

                parameter "Patient Id" String default '12345'

                define function "Are Equal"(left String, right String):
                  left = right
                """,
            )

        assertNoProblems(result)
        assertEquals(2, result.library.definitions.size + result.library.statements.size)
    }

    @Test
    fun parsesPropertyAccessExpression() {
        val result =
            parse(
                """
                library Properties version '1.0'

                define "Patient Name": Patient.name
                """,
            )

        assertNoProblems(result)
        assertEquals(1, result.library.statements.size)
    }

    @Test
    fun parsesBooleanComparisonExpression() {
        val result =
            parse(
                """
                library Booleans version '1.0'

                define "Null Check": true is not null
                define "Equality": 'abc' = 'abc'
                define "Inequality": 5 < 10
                """,
            )

        assertNoProblems(result)
        assertEquals(3, result.library.statements.size)
    }

    @Test
    fun parsesExtensiveExpressionCoverage() {
        val expressions =
            listOf(
                "1 + 2",
                "1 - 2",
                "2 * 3",
                "4 / 2",
                "5 div 2",
                "5 mod 2",
                "2 ^ 3",
                "'foo' & 'bar'",
                "1 = 1",
                "1 != 2",
                "'abc' ~ 'abc'",
                "'abc' !~ 'def'",
                "1 < 2",
                "1 <= 2",
                "2 > 1",
                "2 >= 1",
                "true and false",
                "true or false",
                "true xor false",
                "true implies false",
                "not false",
                "exists { 1, 2 }",
                "{ 1, 2 } union { 3 }",
                "{ 1, 2 } intersect { 2 }",
                "{ 1, 2 } except { 2 }",
                "1 in { 1, 2 }",
                "{ 'a' } contains 'a'",
                "if true then 1 else 0",
                "case when 1 = 1 then 'a' else 'b' end",
                "1 between 0 and 2",
                "duration in days between @2020-01-01T00:00:00 and @2020-01-05T00:00:00",
                "difference in days between @2020-01-05T00:00:00 and @2020-01-01T00:00:00",
                "duration in days of Interval[1, 5]",
                "difference in days of Interval[1, 5]",
                "width of Interval[1, 5]",
                "successor of 1",
                "predecessor of 2",
                "singleton from { 1 }",
                "point from Interval[1, 5]",
                "minimum Integer",
                "maximum Integer",
                "convert 5 to String",
                "start of Interval[1, 5]",
                "end of Interval[1, 5]",
                "duration in days of Interval[@2020-01-01T00:00:00, @2020-01-05T00:00:00]",
                "distinct { 1, 1, 2 }",
                "flatten { { 1 }, { 2 } }",
                "List<Integer>{ 1, 2, 3 }",
                "Tuple { value: 1, flag: true }",
                "System.Code{ code: '140', system: 'http://snomed.info/sct' }",
                "5 'mg'",
                "5 'mg':10 'mL'",
                "@2020-01-01",
                "@2020-01-01T00:00:00",
                "@T10:30:00",
                "null",
                "true",
                "Code '140' from \"SNOMED\"",
                "1 is Integer",
                "1 is not null",
                "1 as Integer",
                "cast 1 as Integer",
                "expand Interval[@2020-01-01T00:00:00, @2020-01-03T00:00:00]",
                "collapse { Interval[@2020-01-01T00:00:00, @2020-01-02T00:00:00], Interval[@2020-01-02T00:00:00, @2020-01-03T00:00:00] }",
            )

        val result = parseExpressions(expressions)
        assertNoProblems(result)
        assertEquals(expressions.size, result.library.statements.size)
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

        val result = parse(queryLibrary)
        assertNoProblems(result)
        assertEquals(4, result.library.statements.size)
    }
}
