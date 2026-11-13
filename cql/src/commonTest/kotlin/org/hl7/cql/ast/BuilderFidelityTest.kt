package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** The AST keeps what is needed to print the source back as it was written. */
class BuilderFidelityTest {

    @Test
    fun keepsHowAnIdentifierIsQuoted() {
        val names =
            Builder()
                .parseLibrary("define A: 1\ndefine \"B\": 2\ndefine `C`: 3")
                .library
                .statements
                .map { (it as ExpressionDefinition).name }
        assertEquals(listOf("A", "B", "C"), names.map { it.value })
        assertEquals(
            listOf(IdentifierStyle.PLAIN, IdentifierStyle.QUOTED, IdentifierStyle.DELIMITED),
            names.map { it.style },
        )
    }

    @Test
    fun keepsTheQuotingOfEachPartOfAQualifiedName() {
        val text = "library Measures.\"Inpatient Encounters\""
        val name = Builder().parseLibrary(text).library.name!!
        assertEquals(listOf("Measures", "Inpatient Encounters"), name.names)
        assertEquals(
            listOf(IdentifierStyle.PLAIN, IdentifierStyle.QUOTED),
            name.parts.map { it.style },
        )
        assertEquals("Measures.\"Inpatient Encounters\"", text.spanOf(name.locator))
    }

    @Test
    fun locatesEveryIdentifierAtTheTextItWasReadFrom() {
        val text =
            """
            library Example.Measures version '1'
            using FHIR version '4.0.1'
            include Common version '1' called "Shared"
            codesystem "LOINC": 'http://loinc.org'
            valueset `Visits`: 'http://example.org/v' codesystems { "LOINC" }
            code "Glucose": '2345-7' from Shared."SNOMED" display 'Glucose'
            concept Labs: { "Glucose" }
            parameter "Measurement Period" Interval<DateTime>
            context Patient
            define Encounters: [Encounter: type in `Visits`] E
              with [Condition] C such that C.subject = E.subject
              where E.status = 'finished'
              return Tuple { id: E.id, code: E.type }
            define Ordered: Encounters O let c: O.code sort by id desc
            define fluent function Clean(value List<FHIR.Coding>, t Tuple { system String }):
              value.distinct().Count()
            define Shared: Shared."Common Definition"
            """
                .trimIndent()
        val result = Builder().parseLibrary(text)
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })

        val identifiers = Json.encodeToJsonElement(LibraryResult.serializer(), result).identifiers()
        assertTrue(identifiers.size > 40, "Found only ${identifiers.size} identifiers")
        for (identifier in identifiers) {
            assertEquals(identifier.written(), text.spanOf(identifier.locator), "$identifier")
        }
    }

    @Test
    fun keepsParentheses() {
        val text = "((1 + 2)) * 3"
        val product = Builder().parseExpression(text).expression as OperatorBinaryExpression
        val outer = product.left as ParenthesizedExpression
        val inner = outer.expression as ParenthesizedExpression
        assertTrue(inner.expression is OperatorBinaryExpression)
        assertEquals("((1 + 2))", text.spanOf(outer.locator))
        assertEquals("(1 + 2)", text.spanOf(inner.locator))
    }

    /** The source text an [Identifier] reads as, given how it was written. */
    private fun Identifier.written(): String =
        when (style) {
            IdentifierStyle.PLAIN -> value
            IdentifierStyle.QUOTED -> "\"$value\""
            IdentifierStyle.DELIMITED -> "`$value`"
        }

    private fun String.spanOf(locator: Locator): String =
        substring(locator.startIndex, locator.stopIndex + 1)

    /**
     * Every [Identifier] in the serialized tree. An identifier is the only object with a string
     * `value` and no `kind`; the literals that also have a `value` are told apart by their `kind`.
     */
    private fun JsonElement.identifiers(): List<Identifier> =
        when (this) {
            is JsonObject -> {
                val value = this["value"]
                val self =
                    if ("kind" !in this && value is JsonPrimitive && value.isString)
                        listOf(Json.decodeFromJsonElement(Identifier.serializer(), this))
                    else emptyList()
                self + values.flatMap { it.identifiers() }
            }
            is JsonArray -> flatMap { it.identifiers() }
            else -> emptyList()
        }
}
