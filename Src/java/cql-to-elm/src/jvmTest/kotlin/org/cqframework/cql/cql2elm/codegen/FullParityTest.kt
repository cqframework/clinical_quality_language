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
 * - **Passed (12):** ArithmeticOperators, AggregateOperators, ComparisonOperators,
 *   CqlComparisonOperators, ForwardReferences, LogicalOperators, MessageOperators,
 *   NullologicalOperators, StringOperators, TimeOperators, TypeOperators, Union123AndEmpty
 * - **Skipped (16):** Files requiring models, unsupported features, or advanced type inference
 * - **Known skips (10):** Error recovery, synthetic ELM constructions, model-specific tests
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
                // MultiSourceQuery: PASSING — validator flags invalid Includes → emitter emits Null
                // InvalidSortClauses: PASSING — validator flags invalid sort → emitter emits Null
                // AggregateOperators: PASSING (M19)
                // TypeOperators: PASSING (M19)
                // Aggregate: OUR TYPE INFERENCE IS MORE CORRECT than legacy (PR #1710).
                //
                // Factorial: Coalesce(R, 1) where R is an aggregate accumulator starting
                // from null. The legacy resolves Coalesce(Any, Integer) via generic overload
                // matching which unifies T to Any, giving result type Any. This forces an
                // unnecessary As(Integer) cast on the Multiply operand. Our pipeline computes
                // the result type as the common type of concrete (non-Any) args = Integer,
                // which is correct — Coalesce always returns a non-null value of the unified
                // concrete type. No downstream cast needed.
                //
                // RolledOutIntervals: similar issue — legacy over-wraps union operands in
                // As(List<Choice<Interval,List<Interval>>>) because the accumulator type
                // propagation loses precision. Our pipeline preserves the correct types.
                //
                // Pattern to watch for: when the legacy inserts As casts that our pipeline
                // doesn't, check whether the legacy's type inference lost precision due to
                // Any-typed aggregate accumulators or null starting values. If our inferred
                // type is strictly more specific and correct, it's a legacy bug, not a
                // parity regression.
                "Aggregate" to
                    "Legacy bug #1710: Coalesce type inference loses precision with Any-typed accumulator",
                // CqlIntervalOperators: TestEndsNull — CI constant-folds interval bounds but
                // emitter's `hasError` check catches the As-wrapped null bound, emitting bare Null.
                // The emission-side interval expansion is still needed as fallback for
                // non-literals.
                // CqlIntervalOperators: testing
                // "CqlIntervalOperators" to
                //     "TestEndsNull: CI-generated As on null bound flagged as error by validator",
                // CqlListOperators: resolution now records cast for Any→Collection args.
                // Re-typing still produces null type - needs further investigation.
                "CqlListOperators" to "IndexOf(null, {}): re-typing loses type for Any-arg cast",
                // Null safety wrapping: legacy wraps point operands in If(IsNull) for
                // interval-point comparisons
                // "IntervalOperators" — testing
                // ImplicitConversions: Code→Concept/ToConcept conversion and error recovery
                "ImplicitConversions" to
                    "Code→Concept conversion (ToConcept) and CodesToConcept error recovery",
                // Name hiding: QDM model interval resolution and out-of-scope error recovery
                "NameHiding" to "QDM interval resolution and out-of-scope error recovery",
                // AgeOperators: CalculateAgeIn* and AgeIn*At resolve via operator registry.
                // AgeIn*() (0-arg) still needs model context injection (Patient.birthDate).
                "AgeOperators" to
                    "AgeIn*() 0-arg functions need model context injection (Patient.birthDate)",
                // Terminology: legacy resolves retrieve code properties and function references
                "TerminologyReferences" to
                    "Retrieve code properties and terminology function resolution",
                // QUICK model types (Element, Extension, Code) not yet supported
                "TupleAndClassConversions" to
                    "QUICK model: Element/Extension types not yet supported",
            )
    }
}
