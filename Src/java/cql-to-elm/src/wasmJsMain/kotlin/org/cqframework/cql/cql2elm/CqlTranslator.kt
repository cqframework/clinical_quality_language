@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.CharStreams
import org.cqframework.cql.elm.serializing.DefaultElmLibraryWriterProvider

@JsExport
fun createCqlTranslator(
    cqlText: String,
    libraryManager: JsReference<BaseLibraryManager>
): JsReference<BaseCqlTranslator> {
    return BaseCqlTranslator(
            null,
            null,
            CharStreams.fromString(cqlText),
            libraryManager.get(),
            DefaultElmLibraryWriterProvider
        )
        .toJsReference()
}

@JsExport
fun cqlTranslatorToJson(cqlTranslator: JsReference<BaseCqlTranslator>): String {
    return cqlTranslator.get().toJson()
}

@JsExport
fun cqlTranslatorToXml(cqlTranslator: JsReference<BaseCqlTranslator>): String {
    return cqlTranslator.get().toXml()
}
