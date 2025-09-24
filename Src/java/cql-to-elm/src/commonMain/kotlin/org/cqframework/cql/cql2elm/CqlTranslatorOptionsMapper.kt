@file:OptIn(ExperimentalSerializationApi::class)

package org.cqframework.cql.cql2elm

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.io.encodeToSink

private val cqlTranslatorOptionsJson = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}

/**
 * Parses the content of cql-options.json into an instance of [CqlTranslatorOptions]. The structure
 * of the JSON object matches that of the [CqlTranslatorOptions] class except that the
 * `cqlCompilerOptions` field is unwrapped so all the fields from the [CqlCompilerOptions] class are
 * stored directly in the root JSON object. We therefore parse the same JSON object twice, once as a
 * [CqlCompilerOptions] and once as a [CqlTranslatorOptions], and then merge the two.
 */
internal fun readTranslatorOptionsFromJson(source: Source): CqlTranslatorOptions {
    val string = source.readString()
    val compilerOptions = cqlTranslatorOptionsJson.decodeFromString<CqlCompilerOptions>(string)
    val translatorOptions = cqlTranslatorOptionsJson.decodeFromString<CqlTranslatorOptions>(string)

    return translatorOptions.withCqlCompilerOptions(compilerOptions)
}

/**
 * Serializes the given [CqlTranslatorOptions] instance into a JSON object with the
 * `cqlCompilerOptions` field unwrapped.
 */
internal fun writeTranslatorOptionsToJson(options: CqlTranslatorOptions, sink: Sink) {
    val jsonElement =
        cqlTranslatorOptionsJson.encodeToJsonElement(CqlTranslatorOptions.serializer(), options)
    require(jsonElement is JsonObject)
    val unwrapped =
        jsonElement.entries
            .flatMap { (key, value) ->
                if (key == "cqlCompilerOptions") {
                    require(value is JsonObject)
                    value.entries.map { it.key to it.value }
                } else {
                    listOf(key to value)
                }
            }
            .associate { it }
    cqlTranslatorOptionsJson.encodeToSink(JsonObject(unwrapped), sink)
}
