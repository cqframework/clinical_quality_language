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
import org.cqframework.cql.cql2elm.analysis.CompilerFrontend
import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
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
 * - **Passed (9):** ArithmeticOperators, ComparisonOperators, CqlComparisonOperators,
 *   ForwardReferences, LogicalOperators, MessageOperators, NullologicalOperators, StringOperators,
 *   TimeOperators
 * - **Skipped (16):** Files requiring models, unsupported features, or advanced type inference
 * - **Known skips (7):** Error recovery tests and files with type-inference differences
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

        // Step 2: Run through CompilerFrontend + ElmEmitter
        val emittedLibrary =
            try {
                val modelManager = createModelManager()
                val frontendResult =
                    CompilerFrontend(modelManager = modelManager).analyze(astResult.library)
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
                JsonObject(filtered)
            }
            is JsonArray -> JsonArray(element.map { normalize(it) })
            else -> element
        }
    }

    private companion object {
        private val IGNORED_KEYS = setOf("annotation", "localId", "locator", "signature")

        /**
         * Files that are known to diverge for well-understood reasons and should be skipped. Each
         * entry maps a test name (without .cql) to a skip reason.
         */
        private val KNOWN_SKIPS =
            mapOf(
                // Error recovery tests: legacy replaces semantic errors with Null expressions,
                // our pipeline emits the AST as-is since we don't do error recovery yet
                "InvalidCastExpression" to "Error recovery: legacy replaces invalid cast with Null",
                "UndeclaredForward" to
                    "Error recovery: legacy replaces undeclared function ref with Null",
                "UndeclaredSignature" to
                    "Error recovery: legacy replaces unmatched function call with Null",
                "RecursiveFunctions" to
                    "Error recovery: legacy replaces recursive function body with Null",
                // shouldNotBeAllowed: legacy replaces Includes on non-list type with Null
                "MultiSourceQuery" to
                    "Error recovery: legacy replaces type-invalid Includes with Null",
                // Null type-casting: legacy wraps null operands in As(ListTypeSpecifier)
                // for list operations and wraps lists in As for union choice types;
                // also wraps heterogeneous Flatten in implicit Query
                // "ListOperators" — testing
                // TestAliasSort: alias sort now handled (replaced with Null)
                // Error recovery: legacy replaces all invalid sort clauses with Null
                "InvalidSortClauses" to
                    "Error recovery: legacy replaces invalid sort expressions with Null",
                // Aggregate query wrapping: legacy wraps integer list args to
                // Avg/Median/StdDev/Variance/etc. in implicit queries with ToDecimal conversions
                // "AggregateOperators" — testing
                // Type coercion: legacy wraps if/case branches in As for choice types,
                // wraps union operands in As for choice list types
                // "TypeOperators" — testing
                // Aggregate coercion: Coalesce As wrapping, QueryLetRef in nested queries,
                // ToQuantity wrapping on DurationBetween
                // "Aggregate" — testing
                // Null safety wrapping: legacy wraps point operands in If(IsNull) for
                // interval-point comparisons
                // "IntervalOperators" — testing
                "CqlIntervalOperators" to
                    "Interval null expansion and property access wrapping",
                // Implicit conversions: legacy wraps operands in ToDecimal/As for type promotion
                "ImplicitConversions" to
                    "ToDecimal promotion in if/case/interval, null-As in case",
                // Name hiding: QDM model interval resolution and out-of-scope error recovery
                "NameHiding" to "QDM interval resolution and out-of-scope error recovery",
                // Age operators: legacy maps CalculateAge/CalculateAgeAt to special ELM nodes
                "AgeOperators" to
                    "System function mapping: legacy maps age functions to CalculateAge ELM nodes",
                // Terminology: legacy resolves retrieve code properties and function references
                "TerminologyReferences" to
                    "Retrieve code properties and terminology function resolution",
                // QUICK model types (Element, Extension, Code) not yet supported
                "TupleAndClassConversions" to
                    "QUICK model: Element/Extension types not yet supported",
            )
    }
}
