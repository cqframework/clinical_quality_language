package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import java.util.*
import org.hl7.elm.r1.VersionedIdentifier

interface LibrarySourceProvider {
    fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source?

    fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): Source? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }
        return null
    }
}

fun getLibrarySourceProviders(refresh: Boolean): Iterator<LibrarySourceProvider> {
    val loader: ServiceLoader<LibrarySourceProvider> =
        ServiceLoader.load(LibrarySourceProvider::class.java)
    if (refresh) {
        loader.reload()
    }
    return loader.iterator()
}