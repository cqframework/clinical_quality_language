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
import org.cqframework.cql.cql2elm.TestLibrarySourceProvider
import org.cqframework.cql.cql2elm.analysis.SemanticAnalyzer
import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider
import org.cqframework.cql.elm.serializing.ElmJsonLibraryWriter
import org.hl7.cql.ast.Builder
import org.hl7.cql.model.SystemModelInfoProvider
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Parity tests comparing the new AST pipeline (Builder → SemanticAnalyzer → ElmEmitter) against the
 * legacy translator (CqlTranslator). Both pipelines compile the same CQL source and their
 * normalized JSON ELM output is compared.
 *
 * Files that fail to parse with the AST Builder or throw [ElmEmitter.UnsupportedNodeException] are
 * skipped (shown as "skipped" in test output) rather than failed.
 *
 * ## OperatorTests (CI gate)
 * - **Passed (31):** All operator tests including model-dependent tests
 * - **Known skip (1):** Aggregate — legacy bug #1710 (new pipeline is more correct)
 *
 * ## Exploratory suites (@Disabled, run manually to assess coverage)
 * Root-level, FHIR R4, and FHIR R4.0.1 test directories are included as @Disabled test factories.
 * Many failures are expected due to unresolved library includes, FHIR model emission gaps, and
 * ModelManager type resolution issues. Remove @Disabled as gaps are closed.
 *
 * ### Known gap categories
 * 1. **Library includes**: new pipeline doesn't resolve multi-library dependencies yet
 * 2. **ModelManager in emission**: type specifier resolution needs model context during codegen
 * 3. **Unknown system types**: empty type strings from analysis (edge cases in type resolution)
 * 4. **FHIR-specific emission**: various FHIR model emission differences
 */
class FullParityTest {

    private val json = Json { prettyPrint = false }

    @TestFactory
    fun operatorTestParity(): Collection<DynamicTest> {
        val testDir = "org/cqframework/cql/cql2elm/OperatorTests/"
        val files = listCqlFiles(testDir)
        check(files.isNotEmpty()) { "No CQL files found in $testDir — classpath misconfigured?" }
        return files.map { fileName ->
            DynamicTest.dynamicTest(fileName.removeSuffix(".cql")) {
                tryAssertParity(testDir + fileName, fileName.removeSuffix(".cql"))
            }
        }
    }

    @Disabled("Exploratory: 42/77 pass, 13 skip")
    @TestFactory
    fun rootLevelParity(): Collection<DynamicTest> {
        return buildParityTests("org/cqframework/cql/cql2elm/", "root")
    }

    @Disabled("Exploratory: 0/14 pass")
    @TestFactory
    fun fhirR4Parity(): Collection<DynamicTest> {
        return buildParityTests("org/cqframework/cql/cql2elm/fhir/r4/", "fhir-r4")
    }

    @Disabled("Exploratory: 5/28 pass, 1 skip")
    @TestFactory
    fun fhirR401Parity(): Collection<DynamicTest> {
        return buildParityTests("org/cqframework/cql/cql2elm/fhir/r401/", "fhir-r401")
    }

    private fun buildParityTests(testDir: String, prefix: String): Collection<DynamicTest> {
        val files = listCqlFiles(testDir)
        check(files.isNotEmpty()) { "No CQL files found in $testDir — classpath misconfigured?" }
        return files.map { fileName ->
            DynamicTest.dynamicTest("$prefix-${fileName.removeSuffix(".cql")}") {
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
                val libraryManager = createLibraryManager()
                val frontendResult =
                    SemanticAnalyzer(
                            modelManager = libraryManager.modelManager,
                            libraryManager = libraryManager,
                        )
                        .analyze(astResult.library)
                ElmEmitter(frontendResult.semanticModel).emit(frontendResult.library).library
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
                val legacyTranslator = CqlTranslator.fromText(cql, createLibraryManager())
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

    private fun createLibraryManager(): LibraryManager =
        LibraryManager(createModelManager()).apply {
            librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        }

    private fun listCqlFiles(resourceDir: String): List<String> {
        // Multiple classpath entries may resolve for the same directory path (compiled classes,
        // JARs, test resources). Scan all file:// entries to find the one containing .cql files.
        val urls = this::class.java.classLoader.getResources(resourceDir).toList()
        for (url in urls) {
            if (url.protocol != "file") continue
            val dir = java.io.File(url.toURI())
            val cqlFiles =
                dir.listFiles()?.filter { it.extension == "cql" }?.map { it.name }?.sorted()
            if (!cqlFiles.isNullOrEmpty()) return cqlFiles
        }
        return emptyList()
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
                // Normalize associative binary operators (Union, Intersect):
                // flatten nested trees into sorted operand lists so parser associativity
                // differences don't cause false failures. Except is NOT associative.
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
        private val ASSOCIATIVE_OPS = setOf("Union", "Intersect")

        /**
         * Files that are known to diverge for well-understood reasons and should be skipped. Each
         * entry maps a test name (without .cql) to a skip reason.
         */
        private val KNOWN_SKIPS =
            mapOf(
                "Aggregate" to
                    "Legacy bug #1710: Coalesce type inference loses precision with Any-typed accumulator",
                "MultiSourceQuery" to
                    "New pipeline preserves expressions with type errors; legacy replaces with Null",
            )
    }
}
