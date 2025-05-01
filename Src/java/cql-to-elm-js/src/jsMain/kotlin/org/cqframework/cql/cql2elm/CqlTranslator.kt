package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cqframework.cql.elm.serializing.DefaultElmLibraryWriterProvider

@OptIn(ExperimentalJsExport::class)
@JsExport
fun getCqlTranslator(content: String, libraryManager: BaseLibraryManager): BaseCqlTranslator {
    return BaseCqlTranslator.fromText(content, libraryManager, DefaultElmLibraryWriterProvider())
}
