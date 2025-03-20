package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.serializersModuleOf
import org.cqframework.cql.elm.serializing.BigDecimalJsonSerializer
import org.cqframework.cql.elm.serializing.NarrativeJsonSerializer

internal val json = Json {
    serializersModule =
        serializersModuleOf(BigDecimalJsonSerializer) +
            serializersModuleOf(NarrativeJsonSerializer) +
            org.hl7.elm.r1.serializersModule +
            org.hl7.cql_annotations.r1.serializersModule
    explicitNulls = false
    ignoreUnknownKeys = true
}
