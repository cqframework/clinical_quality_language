package org.cqframework.cql.cql2elm.ast

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
import org.hl7.cql.ast.Builder
import org.junit.jupiter.api.Test

class ElmEmitterParityTest {

    private val json = Json { prettyPrint = false }

    @Test
    fun `literal expression matches legacy translator`() {
        val cql =
            """
                library Simple version '1.0.0'
                using System
                context Patient
                define Foo: 42
            """
                .trimIndent()

        val astResult = Builder().parseLibrary(cql)
        assertTrue(
            astResult.problems.isEmpty(),
            "AST builder reported issues: ${astResult.problems}",
        )

        val frontendResult = CompilerFrontend().analyze(astResult.library)
        val emittedLibrary = ElmEmitter().emit(frontendResult.library).library

        val legacyTranslator = CqlTranslator.fromText(cql, LibraryManager(ModelManager()))
        val legacyLibrary = requireNotNull(legacyTranslator.toELM())

        val emittedJson = serialize(emittedLibrary)
        val legacyJson = serialize(legacyLibrary)

        val normalizedEmitted = normalize(emittedJson)
        val normalizedLegacy = normalize(legacyJson)

        assertEquals(
            normalizedLegacy,
            normalizedEmitted,
            @Suppress("MaxLineLength")
            "Emitter output differed from the legacy translator.\nEmitter: $normalizedEmitted\nLegacy: $normalizedLegacy",
        )
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
        private val IGNORED_KEYS = setOf("annotation", "localId", "locator")
    }
}
