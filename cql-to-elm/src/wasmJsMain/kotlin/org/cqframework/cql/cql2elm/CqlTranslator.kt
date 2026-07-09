@file:OptIn(ExperimentalJsExport::class, ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm

@JsExport
fun cqlTranslatorFromText(
    cqlText: String,
    libraryManager: JsReference<LibraryManager>,
): JsReference<CqlTranslator> {
    return CqlTranslator.fromText(cqlText, libraryManager.get()).toJsReference()
}

@JsExport
fun cqlTranslatorToJson(cqlTranslator: JsReference<CqlTranslator>): String {
    return cqlTranslator.get().toJson()
}

@JsExport
fun cqlTranslatorToXml(cqlTranslator: JsReference<CqlTranslator>): String {
    return cqlTranslator.get().toXml()
}
