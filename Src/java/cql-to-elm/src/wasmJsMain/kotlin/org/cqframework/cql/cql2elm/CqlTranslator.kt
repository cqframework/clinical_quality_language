@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

@JsExport
fun createCqlTranslator(
    cqlText: String,
    libraryManager: JsReference<BaseLibraryManager>
): JsReference<BaseCqlTranslator> {
    return BaseCqlTranslator.fromText(cqlText, libraryManager.get()).toJsReference()
}

@JsExport
fun cqlTranslatorToJson(cqlTranslator: JsReference<BaseCqlTranslator>): String {
    return cqlTranslator.get().toJson()
}

@JsExport
fun cqlTranslatorToXml(cqlTranslator: JsReference<BaseCqlTranslator>): String {
    return cqlTranslator.get().toXml()
}
