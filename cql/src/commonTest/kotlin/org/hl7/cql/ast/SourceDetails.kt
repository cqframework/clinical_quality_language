package org.hl7.cql.ast

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * [this] without source positions or identifier quoting, which tests of structure do not specify.
 */
fun Expression.withoutSourceDetails(): Expression =
    Json.decodeFromJsonElement(
        Expression.serializer(),
        Json.encodeToJsonElement(Expression.serializer(), this).withoutSourceDetails(),
    )

/** [this] without the `locator` and `style` properties of any node it contains. */
fun JsonElement.withoutSourceDetails(): JsonElement =
    when (this) {
        is JsonObject ->
            JsonObject(
                filterKeys { it != "locator" && it != "style" }
                    .mapValues { it.value.withoutSourceDetails() }
            )
        is JsonArray -> JsonArray(map { it.withoutSourceDetails() })
        else -> this
    }
