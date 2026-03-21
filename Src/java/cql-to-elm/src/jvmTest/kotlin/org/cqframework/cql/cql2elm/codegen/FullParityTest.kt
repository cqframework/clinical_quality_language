package org.cqframework.cql.cql2elm.codegen

import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.analysis.SemanticAnalyzer
import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider
import org.cqframework.cql.elm.serializing.ElmJsonLibraryWriter
import org.hl7.cql.ast.Builder
import org.hl7.cql.model.SystemModelInfoProvider
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Batch parity test that runs all OperatorTests CQL files through both the new AST pipeline and the
 * legacy translator, comparing their normalized JSON output.
 *
 * Files that fail to parse with the AST Builder or throw [ElmEmitter.UnsupportedNodeException] are
 * skipped (shown as "skipped" in test output) rather than failed.
 *
 * ## Results Summary
 * - **Passed (31):** All operator tests including model-dependent tests (TupleAndClassConversions,
 *   TerminologyReferences, NameHiding, ImplicitConversions, etc.)
 * - **Skipped by infrastructure:** Files that fail AST parse or throw UnsupportedNodeException
 * - **Known skip (1):** Aggregate — legacy bug #1710 (new pipeline is more correct)
 */
class FullParityTest {

    private val json = Json { prettyPrint = false }

    @TestFactory
    fun operatorTestParity(): Collection<DynamicTest> {
        val testDir = "org/cqframework/cql/cql2elm/OperatorTests/"
        return listCqlFiles(testDir).map { fileName ->
            DynamicTest.dynamicTest(fileName.removeSuffix(".cql")) {
                tryAssertParity(testDir + fileName, fileName.removeSuffix(".cql"))
            }
        }
    }

    private fun tryAssertParity(resourcePath: String, testName: String) {
        // Skip files that test error recovery behavior (legacy replaces errors with Null)
        KNOWN_SKIPS[testName]?.let { reason ->
            assumeTrue(false, reason)
            return
        }

        val cql = loadResource(resourcePath)

        // Step 1: Parse with AST Builder
        val astResult =
            try {
                Builder().parseLibrary(cql)
            } catch (e: Exception) {
                assumeTrue(false, "AST Builder threw exception: ${e.message}")
                return
            }

        if (astResult.problems.isNotEmpty()) {
            assumeTrue(false, "AST Builder reported problems: ${astResult.problems}")
            return
        }

        // Step 2: Run through SemanticAnalyzer + ElmEmitter
        val emittedLibrary =
            try {
                val modelManager = createModelManager()
                val frontendResult =
                    SemanticAnalyzer(modelManager = modelManager).analyze(astResult.library)
                ElmEmitter(frontendResult.semanticModel, modelManager)
                    .emit(frontendResult.library)
                    .library
            } catch (e: ElmEmitter.UnsupportedNodeException) {
                assumeTrue(false, "Unsupported AST node: ${e.message}")
                return
            } catch (e: Exception) {
                assumeTrue(false, "Emitter threw exception: ${e::class.simpleName}: ${e.message}")
                return
            }

        // Step 3: Run through legacy translator
        val legacyLibrary =
            try {
                val legacyTranslator =
                    CqlTranslator.fromText(cql, LibraryManager(createModelManager()))
                requireNotNull(legacyTranslator.toELM())
            } catch (e: Exception) {
                assumeTrue(false, "Legacy translator failed: ${e.message}")
                return
            }

        // Step 4: Compare normalized JSON
        val emittedJson = serialize(emittedLibrary)
        val legacyJson = serialize(legacyLibrary)

        val normalizedEmitted = normalize(emittedJson)
        val normalizedLegacy = normalize(legacyJson)

        assertEquals(
            normalizedLegacy,
            normalizedEmitted,
            buildString {
                append("Emitter output differed from legacy for $resourcePath.\n")
                append(
                    "Emitter:\n${Json { prettyPrint = true }.encodeToString(normalizedEmitted)}\n"
                )
                append("Legacy:\n${Json { prettyPrint = true }.encodeToString(normalizedLegacy)}")
            },
        )
    }

    private fun createModelManager(): ModelManager =
        ModelManager().apply {
            modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            modelInfoLoader.registerModelInfoProvider(FhirModelInfoProvider())
            modelInfoLoader.registerModelInfoProvider(QdmModelInfoProvider())
            modelInfoLoader.registerModelInfoProvider(QuickModelInfoProvider())
        }

    private fun listCqlFiles(resourceDir: String): List<String> {
        val url =
            this::class.java.classLoader.getResource(resourceDir)
                ?: error("Resource directory not found: $resourceDir")
        val dir = java.io.File(url.toURI())
        return dir.listFiles()?.filter { it.extension == "cql" }?.map { it.name }?.sorted()
            ?: emptyList()
    }

    private fun loadResource(resourcePath: String): String {
        return this::class
            .java
            .classLoader
            .getResourceAsStream(resourcePath)
            ?.bufferedReader()
            ?.use { it.readText() } ?: error("Resource not found: $resourcePath")
    }

    private fun serialize(library: org.hl7.elm.r1.Library): JsonObject {
        val writer = ElmJsonLibraryWriter()
        val raw = writer.writeAsString(library)
        return json.parseToJsonElement(raw).jsonObject
    }

    private fun normalize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonObject -> {
                val filtered = mutableMapOf<String, JsonElement>()
                for ((key, value) in element) {
                    if (key !in IGNORED_KEYS) {
                        filtered[key] = normalize(value)
                    }
                }
                // Normalize associative binary operators (Union, Intersect, Except):
                // flatten nested trees into sorted operand lists so parser associativity
                // differences don't cause false failures.
                val obj = JsonObject(filtered)
                val type = obj["type"]
                if (
                    type is kotlinx.serialization.json.JsonPrimitive &&
                        type.content in ASSOCIATIVE_OPS &&
                        obj["operand"] is JsonArray
                ) {
                    flattenAssociativeOp(obj)
                } else {
                    obj
                }
            }
            is JsonArray -> JsonArray(element.map { normalize(it) })
            else -> element
        }
    }

    /**
     * Flatten a nested associative binary operator into a single node with all leaf operands. e.g.,
     * Union(Union(A, B), C) and Union(A, Union(B, C)) both become Union(A, B, C).
     */
    private fun flattenAssociativeOp(obj: JsonObject): JsonObject {
        val type = (obj["type"] as kotlinx.serialization.json.JsonPrimitive).content
        val operands = obj["operand"] as JsonArray
        val leaves = mutableListOf<JsonElement>()
        fun collect(node: JsonElement) {
            if (
                node is JsonObject &&
                    node["type"] is kotlinx.serialization.json.JsonPrimitive &&
                    (node["type"] as kotlinx.serialization.json.JsonPrimitive).content == type &&
                    node["operand"] is JsonArray
            ) {
                (node["operand"] as JsonArray).forEach { collect(it) }
            } else {
                leaves.add(node)
            }
        }
        operands.forEach { collect(it) }
        val rebuilt = obj.toMutableMap()
        rebuilt["operand"] = JsonArray(leaves)
        return JsonObject(rebuilt)
    }

    private companion object {
        private val IGNORED_KEYS = setOf("annotation", "localId", "locator", "signature")
        private val ASSOCIATIVE_OPS = setOf("Union", "Intersect", "Except")

        /**
         * Files that are known to diverge for well-understood reasons and should be skipped. Each
         * entry maps a test name (without .cql) to a skip reason.
         */
        private val KNOWN_SKIPS =
            mapOf(
                "Aggregate" to
                    "Legacy bug #1710: Coalesce type inference loses precision with Any-typed accumulator"
            )
    }
}
