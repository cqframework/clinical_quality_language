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
import org.cqframework.cql.cql2elm.analysis.SemanticAnalyzer
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.cqframework.cql.elm.serializing.ElmJsonLibraryWriter
import org.cqframework.cql.shared.TestResource
import org.hl7.cql.ast.Builder
import org.hl7.cql.model.SystemModelInfoProvider

/**
 * JVM-only parity tests for FHIR model support. These tests require the FHIR model info provider
 * from the `quick` module which is only available in JVM test dependencies.
 */
class FhirParityTest {

    private val json = Json { prettyPrint = false }

    private fun assertParity(resourcePath: String) {
        val cql = TestResource(resourcePath).readText()

        val modelManager = createModelManager()
        val astResult = Builder().parseLibrary(cql)
        assertTrue(
            astResult.problems.isEmpty(),
            "AST builder reported issues: ${astResult.problems}",
        )

        val frontend = SemanticAnalyzer(modelManager = modelManager)
        val frontendResult = frontend.analyze(astResult.library)
        val emittedLibrary =
            ElmEmitter(frontendResult.semanticModel).emit(frontendResult.library).library

        val legacyTranslator = CqlTranslator.fromText(cql, LibraryManager(createModelManager()))
        val legacyLibrary = requireNotNull(legacyTranslator.toELM())

        val emittedJson = serialize(emittedLibrary)
        val legacyJson = serialize(legacyLibrary)

        val normalizedEmitted = normalize(emittedJson)
        val normalizedLegacy = normalize(legacyJson)

        assertEquals(
            normalizedLegacy,
            normalizedEmitted,
            buildString {
                append("Emitter output differed from legacy for $resourcePath.\n")
                append("Emitter: $normalizedEmitted\n")
                append("Legacy: $normalizedLegacy")
            },
        )
    }

    private fun createModelManager(): ModelManager =
        ModelManager().apply {
            modelInfoLoader.registerModelInfoProvider(SystemModelInfoProvider())
            modelInfoLoader.registerModelInfoProvider(FhirModelInfoProvider())
        }

    @Test
    fun `FhirBasics - FHIR model retrieve matches legacy translator`() {
        assertParity(TEST_RESOURCE_BASE + "FhirBasics.cql")
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
        private val IGNORED_KEYS = setOf("annotation", "localId", "locator", "signature")
    }
}
