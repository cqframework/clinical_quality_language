@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

@JsExport
fun createLibraryManager(
    getModelXml: (id: String, system: String?, version: String?) -> String,
    getLibraryCql: (id: String, system: String?, version: String?) -> String?,
    validateUnit: (unit: String) -> String?
): JsReference<BaseLibraryManager> {
    return BaseLibraryManager.forJs(getModelXml, getLibraryCql, validateUnit).toJsReference()
}

@JsExport
fun libraryManagerAddCompilerOption(
    libraryManager: JsReference<BaseLibraryManager>,
    option: String
) {
    libraryManager.get().addCompilerOption(option)
}

@JsExport
fun libraryManagerRemoveCompilerOption(
    libraryManager: JsReference<BaseLibraryManager>,
    option: String
) {
    libraryManager.get().removeCompilerOption(option)
}
