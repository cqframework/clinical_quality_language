package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import org.hl7.elm.r1.VersionedIdentifier

interface ILibrarySourceLoader {
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
