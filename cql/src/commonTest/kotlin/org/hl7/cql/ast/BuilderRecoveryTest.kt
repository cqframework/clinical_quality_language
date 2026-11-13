package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.hl7.cql.parsetree.createParser

/**
 * A syntax error in one declaration must not change any other. Each case damages one declaration of
 * an otherwise valid library, as an edit in progress would, and checks that the rest parse as they
 * do without the damage.
 */
class BuilderRecoveryTest {

    @Test
    fun aDanglingOperatorDoesNotConsumeTheNextDefine() {
        // `define` is also an identifier, so it was read as the missing operand.
        assertOnlyDamaged(
            clean = "define A: 1 + 2\ndefine B: 2\ndefine C: 3",
            damaged = "define A: 1 +\ndefine B: 2\ndefine C: 3",
            name = "A",
        )
    }

    @Test
    fun anUnclosedQuotedIdentifierDoesNotRunIntoLaterDeclarations() {
        // The quote paired with the next one, several declarations later.
        assertOnlyDamaged(
            clean =
                """
                define Pharyngitis: [Condition: "Acute Pharyngitis"]
                define Antibiotics: [MedicationRequest: "Antibiotic Medications"]
                define Count: Count(Antibiotics)
                """,
            damaged =
                """
                define Pharyngitis: [Condition: "Acute Phar
                define Antibiotics: [MedicationRequest: "Antibiotic Medications"]
                define Count: Count(Antibiotics)
                """,
            name = "Pharyngitis",
        )
    }

    @Test
    fun anUnmatchedQuoteDoesNotDiscardTheRestOfTheLibrary() {
        // With no closing quote at all, the lexer discarded everything it scanned looking for one.
        assertOnlyDamaged(
            clean = "library L\nusing System\ncontext Patient\ndefine Foo: 42",
            damaged = "library L\nusin`g System\ncontext Patient\ndefine Foo: 42",
            name = null,
        )
    }

    @Test
    fun aDamagedDefineDoesNotBreakTheDeclarationBeforeIt() {
        // What is left of the keyword has no boundary to split at, so it ends the previous run.
        assertOnlyDamaged(
            clean = "define A: 1 + 2\ndefine B: 1\ndefine C: 2",
            damaged = "define A: 1 + 2\ndefi\ndefine C: 2",
            name = "B",
        )
    }

    @Test
    fun aQuoteInADefineDoesNotBreakTheDeclarationBeforeIt() {
        // The lexer error lies between the two runs of tokens, so neither run saw it.
        assertOnlyDamaged(
            clean = "define A: X after Y\ndefine B: 1\ndefine C: 2",
            damaged = "define A: X after Y\nde`fine B: 1\ndefine C: 2",
            name = "B",
        )
    }

    @Test
    fun aDamagedDefineAtTheEndDoesNotBreakTheDeclarationBeforeIt() {
        // The error is only reported at the end of the library, after the line that caused it.
        assertOnlyDamaged(
            clean = "define A: X\ndefine B: 1\n",
            damaged = "define A: X\n\nde with\n",
            name = "B",
        )
    }

    @Test
    fun declarationKeywordsUsedAsIdentifiersDoNotStartDeclarations() {
        assertParsesAsAWhole(
            """
            define function ToValue(value Choice<boolean,
                    code,
                    concept>):
              value
            define T: Tuple {
              code: 1,
              context: 2
            }
            define function Name(concept Concept):
              if concept.display is null then 'none'
              else
                concept.display
            """
        )
    }

    @Test
    fun aCleanLibraryParsesAsTheGrammarDoes() {
        val text =
            """
            library L version '1'
            using FHIR version '4.0.1'
            include FHIRHelpers version '4.0.1' called FHIRHelpers
            codesystem "SNOMED": 'http://snomed.info/sct'
            valueset "V": 'http://example.org/v' codesystems { "SNOMED" }
            private code "C": '1' from "SNOMED" display 'One'
            concept "K": { "C" } display 'K'
            parameter P Integer default 1
            /*
            define Commented: 1
            */
            context Patient
            // define Commented: 2
            define private "A": 'multi
            line string'
            define fluent function F(x Integer): x + 1
            """
        assertParsesAsAWhole(text)
        val library = Builder().parseLibrary(text).library
        assertEquals(7, library.definitions.size)
        assertEquals(3, library.statements.size)
    }

    @Test
    fun theLibraryExtentIsTheGrammars() {
        for (text in listOf("", "  // only a comment\n", "  library L\ndefine A: 1\n  ")) {
            val expected = createParser(text).library().let { it.start to it.stop }
            val locator = Builder().parseLibrary(text).library.locator
            assertEquals(expected.first?.startIndex, locator.startIndex, text)
            assertEquals(expected.second?.stopIndex ?: -1, locator.stopIndex, text)
            assertEquals(expected.first?.line, locator.line, text)
        }
    }

    @Test
    fun lexerErrorsArePositioned() {
        val text = "define A: 1\ndefine B: 'abc"
        val problem = Builder().parseLibrary(text).problems.first()
        assertEquals(text.indexOf('\''), problem.locator.startIndex)
        assertEquals(2, problem.locator.line)
    }

    @Test
    fun reportsDeclarationsOutOfOrder() {
        val usingLate = Builder().parseLibrary("define A: 1\nusing FHIR")
        assertTrue(usingLate.problems.any { "must come before" in it.message }, "$usingLate")
        val libraryLate = Builder().parseLibrary("define A: 1\nlibrary L")
        assertTrue(libraryLate.problems.any { "must come first" in it.message }, "$libraryLate")
    }

    @Test
    fun anUnclosedQuoteDoesNotFlipEveryLaterQuote() {
        // Every later string pairs the wrong way round, so each one runs across a line. Recovery
        // once re-lexed the whole library for each of those cuts.
        val clean = (1..DEFINES).joinToString("\n") { "define D$it: 'value $it'" }
        assertOnlyDamaged(clean, clean.replaceFirst("'value 1'", "'value 1"), name = "D1")
    }

    @Test
    fun aLiteralContainingADeclarationLineIsUnterminated() {
        // CQL allows this, but it cannot be told apart from an unclosed quote before a declaration.
        val result = Builder().parseLibrary("define A: 'one\ndefine B: two'")
        assertTrue(result.problems.isNotEmpty())
        assertEquals(listOf("A", "B"), result.library.statements.names())
    }

    @Test
    fun declarationsOnOneLineAreParsedTogether() {
        val result = Builder().parseLibrary("define A: 1 define B: 2")
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        assertEquals(listOf("A", "B"), result.library.statements.names())
    }

    /**
     * Asserts that [damaged] reports a problem and keeps every declaration of [clean] unchanged
     * except the one named [name] (or, for `null`, the first, unnamed one).
     */
    private fun assertOnlyDamaged(clean: String, damaged: String, name: String?) {
        val expected = Builder().parseLibrary(clean.trimIndent())
        assertTrue(expected.problems.isEmpty(), expected.problems.joinToString { it.message })
        val actual = Builder().parseLibrary(damaged.trimIndent())
        assertTrue(actual.problems.isNotEmpty(), "Expected problems for the damaged library")
        assertTrue(actual.problems.none { it.internal }, "Internal errors: ${actual.problems}")
        val kept =
            expected.library.declarations().filterIndexed { index, declaration ->
                if (name == null) index != 0 else declaration.nameOrNull() != name
            }
        val remaining = actual.library.declarations().toMutableList()
        val lost = kept.filterNot { remaining.remove(it) }
        assertTrue(lost.isEmpty(), "Lost or changed: $lost\nin: ${actual.library}")
    }

    /** Asserts that [text] has no problems and gives the same declarations as the grammar. */
    private fun assertParsesAsAWhole(text: String) {
        val result = Builder().parseLibrary(text.trimIndent())
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        val grammar = createParser(text.trimIndent()).library()
        assertEquals(
            grammar.definition().size + grammar.statement().size,
            result.library.definitions.size + result.library.statements.size,
        )
    }

    private fun List<Statement>.names(): List<String> =
        filterIsInstance<ExpressionDefinition>().map { it.name.value }

    /** Declarations in source order, without source positions, which shift with the damage. */
    private fun Library.declarations(): List<JsonElement> =
        (definitions.map { it to Json.encodeToJsonElement(Definition.serializer(), it) } +
                statements.map { it to Json.encodeToJsonElement(Statement.serializer(), it) })
            .sortedBy { it.first.locator.startIndex }
            .map { it.second.withoutSourceDetails() }

    private fun JsonElement.nameOrNull(): String? =
        (((this as JsonObject)["name"] as? JsonObject)?.get("value") as? JsonPrimitive)?.content

    companion object {
        private const val DEFINES = 500
    }
}
