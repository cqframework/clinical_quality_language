package org.hl7.fhirpath

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier

class TestLibrarySourceProvider : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val libraryFileName =
            "stu3/${libraryIdentifier.id}${if (libraryIdentifier.version != null) "-${libraryIdentifier.version}" else ""}.cql"
        val inputStream = TestLibrarySourceProvider::class.java.getResourceAsStream(libraryFileName)
        return inputStream?.asSource()?.buffered()
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }

        return null
    }
}
