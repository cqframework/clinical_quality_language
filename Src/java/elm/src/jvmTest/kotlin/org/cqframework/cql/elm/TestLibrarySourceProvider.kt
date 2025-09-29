package org.cqframework.cql.elm

import java.util.Locale
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier

class TestLibrarySourceProvider(val path: String = "LibraryTests") : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL)
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        val stream =
            TestLibrarySourceProvider::class
                .java
                .getResourceAsStream(getFileName(libraryIdentifier, type)) ?: return null
        return stream.asSource().buffered()
    }

    private fun getFileName(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): String {
        return "%s/%s%s.%s"
            .format(
                Locale.US,
                path,
                libraryIdentifier.id,
                if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version) else "",
                type.toString().lowercase(),
            )
    }
}
