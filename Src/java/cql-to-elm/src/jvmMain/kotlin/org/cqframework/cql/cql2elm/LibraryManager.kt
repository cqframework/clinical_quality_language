package org.cqframework.cql.cql2elm

import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.ucum.lazyUcumService
import org.cqframework.cql.elm.serializing.ElmLibraryReaderProvider
import org.cqframework.cql.elm.serializing.ElmLibraryReaderProviderFactory
import org.hl7.elm.r1.VersionedIdentifier

class LibraryManager
@JvmOverloads
constructor(
    modelManager: ModelManager,
    cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary> = HashMap(),
    elmLibraryReaderProvider: ElmLibraryReaderProvider =
        ElmLibraryReaderProviderFactory.providers(false).next(),
) :
    BaseLibraryManager(
        modelManager,
        modelManager.namespaceManager,
        PriorityLibrarySourceLoader(),
        lazyUcumService,
        cqlCompilerOptions,
        libraryCache,
        elmLibraryReaderProvider,
    ) {
    override val librarySourceLoader: LibrarySourceLoader
        get() = super.librarySourceLoader as LibrarySourceLoader

    override fun getCompilerForLibrary(libraryIdentifier: VersionedIdentifier): BaseCqlCompiler {
        return CqlCompiler(
            libraryIdentifier.system?.let { namespaceManager.getNamespaceInfoFromUri(it) },
            libraryIdentifier,
            this
        )
    }
}
