package org.cqframework.cql.cql2elm

@OptIn(ExperimentalJsExport::class)
@JsExport
class CqlTranslator(
    cqlText: String,
    libraryManager: LibraryManager,
) {
    private val _cqlTranslator: BaseCqlTranslator =
        BaseCqlTranslator.fromText(cqlText, libraryManager._libraryManager)

    fun toJson(): String {
        return _cqlTranslator.toJson()
    }

    fun toXml(): String {
        return _cqlTranslator.toXml()
    }
}
