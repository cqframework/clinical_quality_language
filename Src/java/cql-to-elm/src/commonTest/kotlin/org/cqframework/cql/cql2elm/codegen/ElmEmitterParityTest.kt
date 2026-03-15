package org.cqframework.cql.cql2elm.codegen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.analysis.CompilerFrontend
import org.cqframework.cql.elm.serializing.ElmJsonLibraryWriter
import org.cqframework.cql.shared.TestResource
import org.hl7.cql.ast.Builder
import org.hl7.cql.model.SystemModelInfoProvider

class ElmEmitterParityTest {

    private val json = Json { prettyPrint = false }

    /**
     * Parameterized parity check: parses the CQL with the new AST pipeline and the legacy
     * translator, then asserts that the normalized JSON output matches.
     */
    private fun assertParity(resourcePath: String) {
        val cql = TestResource(resourcePath).readText()

        val astResult = Builder().parseLibrary(cql)
        assertTrue(
            astResult.problems.isEmpty(),
            "AST builder reported issues: ${astResult.problems}",
        )

        val frontendResult = CompilerFrontend().analyze(astResult.library)
        val emittedLibrary =
            ElmEmitter(
                    frontendResult.symbolTable,
                    frontendResult.typeTable,
                    frontendResult.operatorRegistry,
                )
                .emit(frontendResult.library)
                .library

        val legacyTranslator =
            CqlTranslator.fromText(
                cql,
                LibraryManager(
                    ModelManager().apply {
                        modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
                    }
                ),
            )
        val legacyLibrary = requireNotNull(legacyTranslator.toELM())

        val emittedJson = serialize(emittedLibrary)
        val legacyJson = serialize(legacyLibrary)

        val normalizedEmitted = normalize(emittedJson)
        val normalizedLegacy = normalize(legacyJson)

        assertEquals(
            normalizedLegacy,
            normalizedEmitted,
            buildString {
                append("Emitter output differed from the legacy translator for $resourcePath.\n")
                append("Emitter: $normalizedEmitted\n")
                append("Legacy: $normalizedLegacy")
            },
        )
    }

    // ---- Milestone 0+1 tests ----

    @Test
    fun `Simple - literal expression matches legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "Simple.cql")
    }

    @Test
    fun `AllLiterals - all literal types match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "AllLiterals.cql")
    }

    @Test
    fun `ParameterDefs - parameter definitions match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ParameterDefs.cql")
    }

    @Test
    fun `ContextAndAccess - context and access modifiers match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ContextAndAccess.cql")
    }

    // ---- Milestone 2 tests ----

    @Test
    fun `ArithmeticOperators - arithmetic operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ArithmeticOperators.cql")
    }

    @Test
    fun `ComparisonOperators - comparison operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ComparisonOperators.cql")
    }

    // ---- Milestone 3 tests ----

    @Test
    fun `LogicalOperators - logical operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "LogicalOperators.cql")
    }

    @Test
    fun `NullologicalOperators - nullological operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "NullologicalOperators.cql")
    }

    @Test
    fun `StringOperators - string operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "StringOperators.cql")
    }

    // ---- Milestone 4 tests ----

    @Test
    fun `ExpressionReferences - expression references match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ExpressionReferences.cql")
    }

    @Test
    fun `ParameterReferences - parameter references match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ParameterReferences.cql")
    }

    @Test
    fun `ForwardReferences - forward references match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ForwardReferences.cql")
    }

    @Test
    fun `FunctionDefs - function definitions match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "FunctionDefs.cql")
    }

    @Test
    fun `ExternalFunctions - external function definitions match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ExternalFunctions.cql")
    }

    // ---- Milestone 5 tests ----

    @Test
    fun `TypeOperators - type operators and conversions match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "TypeOperators.cql")
    }

    // ---- Milestone 6 tests ----

    @Test
    fun `DateTimeAndIntervals - date time and interval operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "DateTimeAndIntervals.cql")
    }

    // ---- Milestone 7 tests ----

    @Test
    fun `Queries - query expressions match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "Queries.cql")
    }

    // ---- Milestone 8 tests ----

    @Test
    fun `ListOperators - list operators match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "ListOperators.cql")
    }

    // ---- Milestone 10 tests ----

    @Test
    fun `TerminologyDefs - terminology definitions and references match legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "TerminologyDefs.cql")
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
        private const val TEST_RESOURCE_BASE = "org/cqframework/cql/cql2elm/ast/"
        private val IGNORED_KEYS =
            setOf(
                "annotation",
                "localId",
                "locator",
                // Signature is set based on SignatureLevel options, which are not yet configured
                // in the new pipeline. System operators don't need signatures for disambiguation.
                "signature",
            )
    }
}
