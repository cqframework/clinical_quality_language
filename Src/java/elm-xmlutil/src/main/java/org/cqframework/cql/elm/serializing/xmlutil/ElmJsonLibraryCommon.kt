package org.cqframework.cql.elm.serializing.xmlutil

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.serializersModuleOf
import org.hl7.elm_modelinfo.r1.serializing.BigDecimalJsonSerializer

val json = Json {
    serializersModule =
        serializersModuleOf(BigDecimalJsonSerializer) +
            org.hl7.elm.r1.serializersModule +
            org.hl7.cql_annotations.r1.serializersModule
    explicitNulls = false
    ignoreUnknownKeys = true
}
