package org.hl7.cql.ast

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * A syntax error in one definition or statement must not change any other. Editors parse incomplete
 * source on every keystroke, and diagnostics, outlines and navigation for the rest of the library
 * depend on it surviving.
 */
class BuilderRecoveryTest {

    private val repoRoot: File =
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .first { File(it, "settings.gradle.kts").exists() && File(it, "grammar").isDirectory }

    private val excludedDirs =
        setOf("build", "archive", "node_modules", ".git", ".gradle", ".claude")

    /** Every [SAMPLE_EVERY]th repository file that parses without problems. */
    private val sample: List<Pair<File, String>> =
        repoRoot
            .walkTopDown()
            .onEnter { it.name !in excludedDirs }
            .filter { it.isFile && it.extension == "cql" }
            .sortedBy { it.path }
            .map { it to it.readText() }
            .filter { (_, text) -> Builder().parseLibrary(text).problems.isEmpty() }
            .filterIndexed { index, _ -> index % SAMPLE_EVERY == 0 }
            .toList()

    @Test
    fun aDanglingOperatorAffectsOnlyItsOwnStatement() {
        assertRecovers { text, item -> text.insert(item.locator.stopIndex + 1, " +") }
    }

    @Test
    fun aTruncatedStatementAffectsOnlyItself() {
        assertRecovers { text, item ->
            val start = item.locator.startIndex
            val stop = item.locator.stopIndex
            text.removeRange(start + (stop - start + 1) / 2, stop + 1)
        }
    }

    private fun assertRecovers(corrupt: (String, AstNode) -> String) {
        assertTrue(sample.size > 20, "Expected a sample of the repository, found ${sample.size}")
        val failures = mutableListOf<String>()
        var cases = 0
        for ((file, text) in sample) {
            val original = Builder().parseLibrary(text).library
            val items = original.items()
            for (index in pickIndices(items.size)) {
                val parsed = Builder().parseLibrary(corrupt(text, items[index]))
                // The damage can leave valid CQL with another meaning, such as a truncated name
                // that reads as an alias of the expression before it. There is nothing to recover.
                if (parsed.problems.isEmpty()) continue
                cases++
                val result = parsed.library
                val expected = (items.take(index) + items.drop(index + 1)).map { it.normalized() }
                val actual = result.items().map { it.normalized() }.toMutableList()
                val lost = expected.filterNot { actual.remove(it) }
                if (lost.isNotEmpty()) {
                    failures +=
                        "${file.relativeTo(repoRoot)} item $index lost ${lost.size}: " +
                            lost.joinToString { it.describe() }
                }
            }
        }
        if (failures.isNotEmpty()) {
            fail(
                "${failures.size} of $cases corruptions lost other statements:\n" +
                    failures.take(MAX_REPORTED).joinToString("\n")
            )
        }
    }

    /** The first, middle and last statement, which cover the header, body and end of the file. */
    private fun pickIndices(size: Int): Set<Int> =
        if (size == 0) emptySet() else setOf(0, size / 2, size - 1)

    private fun Library.items(): List<AstNode> =
        (definitions + statements).sortedBy { it.locator.startIndex }

    /** A declaration's content without source positions, which shift with the corruption. */
    private fun AstNode.normalized(): JsonElement =
        when (this) {
            is Definition -> Json.encodeToJsonElement(Definition.serializer(), this)
            is Statement -> Json.encodeToJsonElement(Statement.serializer(), this)
            else -> error("Not a declaration: $this")
        }.withoutLocators()

    private fun JsonElement.withoutLocators(): JsonElement =
        when (this) {
            is JsonObject ->
                JsonObject(filterKeys { it != "locator" }.mapValues { it.value.withoutLocators() })
            is JsonArray -> JsonArray(map { it.withoutLocators() })
            else -> this
        }

    private fun JsonElement.describe(): String =
        (this as JsonObject).let { "${it["kind"]} ${it["name"] ?: ""}" }

    private fun String.insert(index: Int, value: String): String =
        substring(0, index) + value + substring(index)

    companion object {
        private const val SAMPLE_EVERY = 5
        private const val MAX_REPORTED = 40
    }
}
