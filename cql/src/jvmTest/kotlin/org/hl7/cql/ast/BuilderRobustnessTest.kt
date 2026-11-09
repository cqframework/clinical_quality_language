package org.hl7.cql.ast

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * The Builder must never throw, whatever the input. Malformed source produces [Problem]s and error
 * nodes instead. Editors call the Builder on every keystroke, so most of the input it sees is
 * incomplete.
 */
class BuilderRobustnessTest {

    private val repoRoot: File =
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .first { File(it, "settings.gradle.kts").exists() && File(it, "grammar").isDirectory }

    private val excludedDirs =
        setOf("build", "archive", "node_modules", ".git", ".gradle", ".claude")

    private val corpus: List<File> =
        repoRoot
            .walkTopDown()
            .onEnter { it.name !in excludedDirs }
            .filter { it.isFile && it.extension == "cql" }
            .sortedBy { it.path }
            .toList()

    /** Files with a wide range of syntax, used as the source of truncated inputs. */
    private val truncationSources =
        listOf(File(repoRoot, "examples/ChlamydiaScreeningCDS.cql")) +
            listOf(
                    "ArithmeticOperators.cql",
                    "DateTimeOperators.cql",
                    "Query.cql",
                    "IntervalOperatorPhrases.cql",
                    "TypeOperators.cql",
                )
                .map { File(repoRoot, "$OPERATOR_TESTS/$it") }

    @Test
    fun parsesEveryCqlFileInTheRepository() {
        assertTrue(corpus.size > 100, "Expected the repository corpus, found ${corpus.size} files")
        val failures =
            corpus.mapNotNull { file -> failureOf(file.readText())?.let { "$file: $it" } }
        if (failures.isNotEmpty()) {
            fail("Builder threw on ${failures.size} files:\n${failures.joinToString("\n")}")
        }
    }

    @Test
    fun parsesTruncatedInput() {
        val failures = mutableListOf<String>()
        for (file in truncationSources) {
            val text = file.readText()
            val stride = maxOf(1, text.length / MAX_CUTS_PER_FILE)
            for (end in 0..text.length step stride) {
                failureOf(text.substring(0, end))?.let {
                    failures += "${file.name} truncated at $end: $it"
                }
            }
        }
        if (failures.isNotEmpty()) {
            fail(
                "Builder threw on ${failures.size} truncated inputs:\n" +
                    failures.take(MAX_REPORTED).joinToString("\n")
            )
        }
    }

    @Test
    fun parsesMalformedDefinitions() {
        val inputs =
            listOf(
                "valueset \"X\" = '1.2.3'",
                "codesystem \"X\" = 'http://loinc.org'",
                "define \"A\": 1 +\ndefine \"B\": 2",
                "define \"A\": [Observation: ]",
                "define \"A\": from [Observation] O where",
                "define \"A\": Interval[1, ",
                "define \"A\": if true then 1",
                "define \"A\": case when",
                "define \"A\": X is not",
                "define \"A\": 1 'mg' + ",
                "define function F(x Integer):",
                "define \"A\": Tuple { a: }",
                "define \"A\": X included in",
                "define \"A\": X starts 3 days before",
                "library",
                "library L version",
                "code \"C\": '1' from \"S\" display",
                "define \"A\": Code '1' from \"S\" display",
                "define \"A\": case when then 5 else null end",
                "define \"A\": [Observation: code in] O",
                "define \"A\": X same day as",
                "context",
            )
        val failures = inputs.mapNotNull { input -> failureOf(input)?.let { "`$input`: $it" } }
        if (failures.isNotEmpty()) {
            fail("Builder threw on ${failures.size} inputs:\n${failures.joinToString("\n")}")
        }
        val unreported = inputs.filter { Builder().parseLibrary(it).problems.isEmpty() }
        assertTrue(unreported.isEmpty(), "Expected problems for: $unreported")
    }

    @Test
    fun keepsAnEmptyVersion() {
        val result = Builder().parseLibrary("include L version '' called M")
        assertTrue(result.problems.isEmpty(), result.problems.joinToString { it.message })
        val include = result.library.definitions.single() as IncludeDefinition
        assertEquals(VersionSpecifier(""), include.version)
    }

    @Suppress("TooGenericExceptionCaught")
    /** Parses [text] and serializes the result, as tooling does through [inspectCqlAst]. */
    private fun failureOf(text: String): String? =
        try {
            inspectCqlAst(text)
            null
        } catch (e: Exception) {
            val site = e.stackTrace.firstOrNull { it.className.startsWith("org.hl7.cql.ast") }
            "${e::class.simpleName}: ${e.message} at $site"
        }

    companion object {
        private const val OPERATOR_TESTS =
            "cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/OperatorTests"
        private const val MAX_CUTS_PER_FILE = 200
        private const val MAX_REPORTED = 50
    }
}
