package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.ucum.getUcumService
import org.hl7.elm.r1.VersionedIdentifier
import kotlin.jvm.JvmOverloads

class LibraryManager
@JvmOverloads
constructor(
    modelManager: ModelManager,
    cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary> = HashMap()
): CommonLibraryManager(
    modelManager,
    modelManager.namespaceManager,
    PriorityLibrarySourceLoader(),
    getUcumService(),
    cqlCompilerOptions,
    libraryCache
) {
    override val librarySourceLoader: LibrarySourceLoader
        get() = super.librarySourceLoader as LibrarySourceLoader
}