package org.cqframework.cql.cql2elm

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.ucum.lazyUcumService
import org.hl7.elm.r1.VersionedIdentifier

class LibraryManager
@JvmOverloads
constructor(
    modelManager: ModelManager,
    cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary> = HashMap()
) :
    CommonLibraryManager(
        modelManager,
        modelManager.namespaceManager,
        PriorityLibrarySourceLoader(),
        lazyUcumService,
        cqlCompilerOptions,
        libraryCache
    ) {
    override val librarySourceLoader: LibrarySourceLoader
        get() = super.librarySourceLoader as LibrarySourceLoader

    override fun getCompilerForLibrary(libraryIdentifier: VersionedIdentifier): CommonCqlCompiler {
        return CqlCompiler(
            libraryIdentifier.system?.let { namespaceManager.getNamespaceInfoFromUri(it) },
            libraryIdentifier,
            this
        )
    }
}
