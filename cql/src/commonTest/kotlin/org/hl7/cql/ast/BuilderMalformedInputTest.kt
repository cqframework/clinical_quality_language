package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * The Builder must never throw, whatever the input: malformed source produces [Problem]s and error
 * nodes instead. Each input here once made it throw.
 */
class BuilderMalformedInputTest {

    /**
     * When the parser cannot complete a rule it leaves out the remaining children, and the
     * generated accessor for a required child asserts that it is present.
     */
    @Test
    fun reportsIncompleteDeclarations() {
        assertReportedWithoutThrowing(
            "library",
            "context",
            "include Common.",
            "codesystem \"X\" = 'http://loinc.org'",
            "valueset \"X\" = '1.2.3'",
            "valueset \"V\": '1' codesystems { \"X\",",
            "valueset \"V\": '1' codesystems {",
            "code \"C\": '1' from",
            "code \"C\": 'c' from \"S\" display ty'",
        )
    }

    @Test
    fun reportsIncompleteExpressions() {
        assertReportedWithoutThrowing(
            "define \"A\": if true then 1",
            "define \"A\": case when",
            "define \"A\": case when then 5 else null end",
            "define \"A\": Tuple { a",
            "define \"A\": Tuple { a: 1, b",
            "define \"A\": Tuple { type: Code '1' from \"S\" display 'Scr",
            "define \"A\": FHIR.Patient { a",
            "define \"A\": X properly between 1",
            "define \"A\": difference in days between X",
        )
    }

    @Test
    fun reportsIncompleteQueryClauses() {
        assertReportedWithoutThrowing(
            "define \"A\": [Encounter] E with [Condition] C",
            "define \"A\": [Encounter] E without [Condition] C",
            "define \"A\": [Encounter] E let",
            "define \"A\": [Encounter] E aggregate",
            "define \"A\": [Encounter] E aggregate R starting",
        )
    }

    @Test
    fun reportsAMalformedVersion() {
        assertReportedWithoutThrowing("include L version ''1' called M")
    }

    @Test
    fun acceptsAnEmptyIdentifierAndVersion() {
        val result = Builder().parseLibrary("include L version '' called M\ndefine \"\": ''")
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        val include = result.library.definitions.single() as IncludeDefinition
        assertEquals(VersionSpecifier(""), include.version)
        assertEquals(
            Identifier(""),
            (result.library.statements.single() as ExpressionDefinition).name,
        )
    }

    @Test
    fun serializesBooleanTests() {
        // A property of `BooleanTestExpression` once clashed with the `kind` class discriminator.
        val json = inspectCqlAst("define \"A\": X is null\ndefine \"B\": X is not true")
        assertTrue("\"booleanTestKind\":\"null\"" in json, json)
        assertTrue("\"booleanTestKind\":\"true\"" in json, json)
    }

    /** Asserts that each input reports a problem, and that its result serializes. */
    private fun assertReportedWithoutThrowing(vararg inputs: String) {
        val failures =
            inputs.mapNotNull { input ->
                try {
                    val result = Builder().parseLibrary(input)
                    inspectCqlAst(input)
                    "`$input` reported no problems".takeIf { result.problems.isEmpty() }
                } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                    "`$input` threw $e"
                }
            }
        if (failures.isNotEmpty()) fail(failures.joinToString("\n"))
    }
}
