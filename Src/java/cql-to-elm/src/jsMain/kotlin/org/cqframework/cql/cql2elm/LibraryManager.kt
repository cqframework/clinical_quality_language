package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.hl7.cql.model.ModelIdentifier
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
) {
    @Suppress("VariableNaming")
    internal val _libraryManager =
        BaseLibraryManager.forJs(
            getModelXml,
            getLibraryCql,
            validateUnit,
            CqlCompilerOptions.defaultOptions(),
            modelCache,
            libraryCache,
        )

    fun addCompilerOption(option: String) {
        _libraryManager.addCompilerOption(option)
    }

    fun removeCompilerOption(option: String) {
        _libraryManager.removeCompilerOption(option)
    }

    fun setSignatureLevel(signatureLevel: String) {
        _libraryManager.cqlCompilerOptions.signatureLevel =
            LibraryBuilder.SignatureLevel.valueOf(signatureLevel)
        _libraryManager.cqlCompilerOptions.compatibilityLevel
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
