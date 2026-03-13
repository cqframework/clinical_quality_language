package org.cqframework.cql.cql2elm.ast

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
import org.cqframework.cql.cql2elm.frontend.CompilerFrontend
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
            ElmEmitter(frontendResult.symbolTable, frontendResult.typeTable)
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
        private val IGNORED_KEYS = setOf("annotation", "localId", "locator")
    }
}
