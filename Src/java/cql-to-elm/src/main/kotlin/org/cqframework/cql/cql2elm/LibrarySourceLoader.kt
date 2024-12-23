package org.cqframework.cql.cql2elm

import java.io.InputStream
import org.hl7.elm.r1.VersionedIdentifier

/** @author mhadley */
interface LibrarySourceLoader {
    fun clearProviders()

    fun getLibrarySource(libraryIdentifier: VersionedIdentifier): InputStream?

    fun registerProvider(provider: LibrarySourceProvider)

    fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): InputStream? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }
        return null
    }
}
