package org.cqframework.cql.elm.evaluation

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier

class TestLibrarySourceProvider : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val libraryFileName =
            String.format(
                "%s.cql",
                libraryIdentifier.id,
            ) // , libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion())
        // : "");
        val inputStream = TestLibrarySourceProvider::class.java.getResourceAsStream(libraryFileName)
        return if (inputStream == null) null else inputStream.asSource().buffered()
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
