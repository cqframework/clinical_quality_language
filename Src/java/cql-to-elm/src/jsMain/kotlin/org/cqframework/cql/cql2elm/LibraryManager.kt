package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.ucum.createUcumService
import org.cqframework.cql.elm.serializing.DefaultElmLibraryReaderProvider
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier

@OptIn(ExperimentalJsExport::class)
@JsExport
@Suppress("NON_EXPORTABLE_TYPE")
class LibraryManager(
    getModelXml: (id: String, system: String?, version: String?) -> String?,
    getLibraryCql: (id: String, system: String?, version: String?) -> String? = { _, _, _ -> null },
    validateUnit: (unit: String) -> String? = { null },
    modelCache: MutableMap<ModelIdentifier, Model> = HashMap(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary> = HashMap(),
) :
    BaseLibraryManager(
        createModelManager(getModelXml, modelCache),
        NamespaceManager(),
        createLibrarySourceLoader(getLibraryCql),
        lazy { createUcumService(validateUnit) },
        CqlCompilerOptions.defaultOptions(),
        libraryCache,
        DefaultElmLibraryReaderProvider
    ) {

    fun addCompilerOption(option: String) {
        addCompilerOptionInner(option)
    }

    fun removeCompilerOption(option: String) {
        removeCompilerOptionInner(option)
    }

    fun setSignatureLevel(signatureLevel: String) {
        cqlCompilerOptions.signatureLevel = LibraryBuilder.SignatureLevel.valueOf(signatureLevel)
    }

    companion object {
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun createModelCache(): MutableMap<ModelIdentifier, Model> {
            return HashMap()
        }

        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun createLibraryCache(): MutableMap<VersionedIdentifier, CompiledLibrary> {
            return HashMap()
        }
    }
}
