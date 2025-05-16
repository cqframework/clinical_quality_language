package org.cqframework.cql.cql2elm

@OptIn(ExperimentalJsExport::class)
@JsExport
class LibraryManager(
    getModelXml: (id: String, system: String?, version: String?) -> String,
    getLibraryCql: (id: String, system: String?, version: String?) -> String? = { _, _, _ -> null },
    validateUnit: (unit: String) -> String? = { null }
) {
    @Suppress("VariableNaming")
    internal val _libraryManager =
        BaseLibraryManager.forJs(getModelXml, getLibraryCql, validateUnit)

    fun addCompilerOption(option: String) {
        _libraryManager.addCompilerOption(option)
    }

    fun removeCompilerOption(option: String) {
        _libraryManager.removeCompilerOption(option)
    }
}
