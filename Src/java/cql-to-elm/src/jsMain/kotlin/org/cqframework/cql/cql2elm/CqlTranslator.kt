package org.cqframework.cql.cql2elm

import org.antlr.v4.kotlinruntime.CharStreams
import org.cqframework.cql.elm.serializing.DefaultElmLibraryWriterProvider

@OptIn(ExperimentalJsExport::class)
@JsExport
class CqlTranslator(
    cqlText: String,
    libraryManager: LibraryManager,
) {
    private val baseCqlTranslator =
        BaseCqlTranslator(
            null,
            null,
            CharStreams.fromString(cqlText),
            libraryManager,
            DefaultElmLibraryWriterProvider
        )

    fun toXml(): String {
        return baseCqlTranslator.toXml()
    }

    fun toJson(): String {
        return baseCqlTranslator.toJson()
    }
}
