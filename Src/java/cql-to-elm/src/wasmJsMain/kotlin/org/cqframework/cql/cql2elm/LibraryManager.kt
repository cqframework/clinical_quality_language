@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.ucum.createUcumService
import org.cqframework.cql.elm.serializing.DefaultElmLibraryReaderProvider
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier

@JsExport
fun createLibraryManager(
    getModelXml: (id: String, system: String?, version: String?) -> String?,
    getLibraryCql: (id: String, system: String?, version: String?) -> String?,
    validateUnit: (unit: String) -> String?,
    modelCache: JsReference<MutableMap<ModelIdentifier, Model>>,
    libraryCache: JsReference<MutableMap<VersionedIdentifier, CompiledLibrary>>,
): JsReference<BaseLibraryManager> {
    return BaseLibraryManager(
            createModelManager(getModelXml, modelCache.get()),
            NamespaceManager(),
            createLibrarySourceLoader(getLibraryCql),
            lazy { createUcumService(validateUnit) },
            CqlCompilerOptions.defaultOptions(),
            libraryCache.get(),
            DefaultElmLibraryReaderProvider
        )
        .toJsReference()
}

@JsExport
fun libraryManagerAddCompilerOption(
    libraryManager: JsReference<BaseLibraryManager>,
    option: String
) {
    libraryManager.get().addCompilerOptionInner(option)
}

@JsExport
fun libraryManagerRemoveCompilerOption(
    libraryManager: JsReference<BaseLibraryManager>,
    option: String
) {
    libraryManager.get().removeCompilerOptionInner(option)
}

@JsExport
fun libraryManagerSetSignatureLevel(
    libraryManager: JsReference<BaseLibraryManager>,
    signatureLevel: String
) {
    libraryManager.get().cqlCompilerOptions.signatureLevel =
        LibraryBuilder.SignatureLevel.valueOf(signatureLevel)
}

@JsExport
fun createModelCache(): JsReference<MutableMap<ModelIdentifier, Model>> {
    return HashMap<ModelIdentifier, Model>().toJsReference()
}

@JsExport
fun createLibraryCache(): JsReference<MutableMap<VersionedIdentifier, CompiledLibrary>> {
    return HashMap<VersionedIdentifier, CompiledLibrary>().toJsReference()
}
