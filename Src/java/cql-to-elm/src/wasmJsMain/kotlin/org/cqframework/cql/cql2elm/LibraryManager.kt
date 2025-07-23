@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm.r1.VersionedIdentifier

@JsExport
fun createLibraryManager(
    getModelXml: (id: String, system: String?, version: String?) -> String?,
    getLibraryCql: (id: String, system: String?, version: String?) -> String?,
    validateUnit: (unit: String) -> String?,
    modelCache: JsReference<MutableMap<ModelIdentifier, Model>>,
    libraryCache: JsReference<MutableMap<VersionedIdentifier, CompiledLibrary>>,
): JsReference<BaseLibraryManager> {
    return BaseLibraryManager.forJs(
            getModelXml,
            getLibraryCql,
            validateUnit,
            CqlCompilerOptions.defaultOptions(),
            modelCache.get(),
            libraryCache.get(),
        )
        .toJsReference()
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

@JsExport
fun createModelCache(): JsReference<MutableMap<ModelIdentifier, Model>> {
    return HashMap<ModelIdentifier, Model>().toJsReference()
}

@JsExport
fun createLibraryCache(): JsReference<MutableMap<VersionedIdentifier, CompiledLibrary>> {
    return HashMap<VersionedIdentifier, CompiledLibrary>().toJsReference()
}
