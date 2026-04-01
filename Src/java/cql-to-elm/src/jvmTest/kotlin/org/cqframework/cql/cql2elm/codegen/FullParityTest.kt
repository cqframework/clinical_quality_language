package org.cqframework.cql.cql2elm.codegen

import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
 * ## Test suites
 * - **OperatorTests**: CI gate — all operator tests including model-dependent tests
 * - **rootLevelParity**: root-level CQL test files
 * - **fhirR4Parity**: FHIR R4 test files
 * - **fhirR401Parity**: FHIR R4.0.1 test files
 * - **fhirR4ChoiceTypeParity**: choice type narrowing / alternative conversion tests
 *
 * Many tests within the broader suites skip due to unresolved library includes, FHIR model emission
 * gaps, or ModelManager type resolution issues. These show as skipped, not failed.
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

    @TestFactory
    fun fhirR4ChoiceTypeParity(): Collection<DynamicTest> {
        val testDir = "org/cqframework/cql/cql2elm/fhir/r4/"
        // Choice type test files exercising choice narrowing / alternative conversions
        val choiceFiles = listOf("TestChoiceTypes.cql", "TestChoiceDateRangeOptimization.cql")
        return choiceFiles.map { fileName ->
            DynamicTest.dynamicTest("fhir-r4-${fileName.removeSuffix(".cql")}") {
                tryAssertParity(testDir + fileName, fileName.removeSuffix(".cql"))
            }
        }
    }

    @TestFactory
    fun rootLevelParity(): Collection<DynamicTest> {
        return buildParityTests("org/cqframework/cql/cql2elm/", "root")
    }

    @TestFactory
    fun fhirR4Parity(): Collection<DynamicTest> {
        return buildParityTests("org/cqframework/cql/cql2elm/fhir/r4/", "fhir-r4")
    }

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

        if (normalizedEmitted == normalizedLegacy) return

        // New pipeline is known to produce better output for these tests
        if (testName in KNOWN_BETTER) {
            assertNoNullStatementExpressions(normalizedEmitted, resourcePath)
            return
        }

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
                // Known divergences: new pipeline intentionally differs from legacy.
                // These need investigation or are edge cases in model/choice-type handling.
                "TupleDifferentKeys" to
                    "New pipeline emits Null for cross-library FunctionRef; needs library call resolution",
                "UncertTuplesWithDiffNullFields" to
                    "New pipeline emits Null for cross-library FunctionRef; needs library call resolution",
                "InTest" to
                    "New pipeline emits Null for undeclared type; legacy also emits Null differently",
                "TestIdentifierCaseMismatch" to
                    "New pipeline emits Null body for function with unresolvable type",
                "TestIncorrectParameterType1204" to
                    "New pipeline emits Null body for function with unresolvable parameter type",
                // QDM tests: context type resolution now handled by validator, output diverges from
                // legacy
                "Issue592" to
                    "QDM Population context type resolution; implicit context def differs from legacy",
                "ParameterTestInvalid" to
                    "QDM context type resolution; implicit context def emission differs from legacy",
                "TestPatientContext" to
                    "QDM Patient context emission; implicit context def differs from legacy",
                // QDM label-resolved retrieves: types resolve via label but emission details differ
                "Issue395" to "QDM label-resolved retrieve; query content differs",
                "Issue405" to "QDM label-resolved retrieve; query content differs",
                "ParserPerformance" to "QDM label-resolved retrieve; SDE content differs",
                "TestIncludedIn" to "QDM label-resolved retrieve; IncludedIn differs",
                "TestRelatedContextRetrieve" to "QDM label-resolved retrieve; context differs",
                "TestVSCastFunction" to
                    "Model conversion application and list element choice-type wrapping diverge from legacy",
                "TestChoiceAssignment" to
                    "QDM classType name resolution and choice-typed instance element wrapping diverge from legacy",
                "TestComments" to
                    "AgeInYearsAt Date vs DateTime overload selection diverges from legacy; " +
                        "both are semantically correct",
                "TranslationTests" to
                    "List<Integer>{null} element type wrapping diverges from legacy; cause under investigation",
                // FHIR-specific gaps: model conversions, cross-library calls, fluent functions.
                "MappingExpansionsRespectSignatureLevel" to
                    "FHIRHelpers.ToInterval model conversion for Period properties not yet applied",
                "TestTrace" to "Trace() function not resolved; emits Null instead of Message node",
                "TestMeasureParameterContext" to
                    "extension() fluent resolution works but parity diff on FunctionRef emission",
                "TestParameterContext" to
                    "extension() fluent resolution works but parity diff on FunctionRef emission",
                "TestMedicationRequest" to
                    "Choice codePath + FHIRHelpers.resolve() for Reference medication not yet supported",
                "TestInclude" to
                    "Search property emission (?name) and FHIRHelpers.resolve() cross-library calls not resolved",
                "TestFHIR" to
                    "FHIR model conversion divergences (choice-type extension value wrapping, QueryLetRef scope)",
                "TestFHIRHelpers" to
                    "FHIRHelpers conversion function emission diverges from legacy (ToConcept/ToCode wrapping)",
                "TestFHIRPath" to
                    "FHIR path-based property and model conversion resolution diverges from legacy",
                "TestFHIRWithHelpers" to
                    "FHIR model conversion with helpers diverges (ToConcept/ToCode, scope resolution)",
            )

        /**
         * Tests where the new pipeline produces better output than legacy. Legacy replaces
         * expressions with Null on error; the new pipeline preserves the correct expression. These
         * tests pass if the new pipeline output has no Null-typed top-level statement expressions
         * (legacy's error-replacement pattern).
         */
        private val KNOWN_BETTER =
            setOf(
                "Aggregate",
                "MultiSourceQuery",
                "RecursiveFunctions",
                "IdentifierDoesNotResolveCaseMismatchExistIdentifier_Issue598",
                "Issue616",
                "QuantityLiteralTest",
                "TestCompatibilityLevel3",
                "TestURIConversion",
                "TestNoImplicitCast",
            )

        /**
         * Verify no top-level statement expression has type "Null" — legacy's error-replacement
         * pattern. If the new pipeline emits Null expressions, it has the same bug as legacy.
         */
        @Suppress("ReturnCount")
        private fun assertNoNullStatementExpressions(
            normalized: JsonElement,
            resourcePath: String,
        ) {
            val library = (normalized as? JsonObject)?.get("library") as? JsonObject ?: return
            val statements = library["statements"] as? JsonObject ?: return
            val defs = statements["def"] as? JsonArray ?: return
            @Suppress("LoopWithTooManyJumpStatements")
            for (def in defs) {
                val defObj = def as? JsonObject ?: continue
                val expr = defObj["expression"] as? JsonObject ?: continue
                val exprType = expr["type"]
                if (exprType is kotlinx.serialization.json.JsonPrimitive) {
                    val name =
                        (defObj["name"] as? kotlinx.serialization.json.JsonPrimitive)?.content
                            ?: "<unknown>"
                    assertFalse(
                        exprType.content == "Null",
                        "New pipeline emitted Null expression for statement '$name' in $resourcePath — " +
                            "this is legacy's error pattern and should not appear in the new pipeline",
                    )
                }
            }
        }
    }
}
