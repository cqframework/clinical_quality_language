package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Clone of the [TestLibrarySourceProvider] that does not enforce versioning in the file names and
 * thus will support tests that do not specify a version in the library identifier.
 */
class TestLibrarySourceVersionAgnosticProvider(val path: String = "LibraryTests") :
    LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL)
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        val inputStream =
            TestLibrarySourceVersionAgnosticProvider::class
                .java
                .getResourceAsStream(getFileName(libraryIdentifier, type))
        return inputStream?.asSource()?.buffered()
    }

    private fun getFileName(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): String {
        return "$path/${libraryIdentifier.id}.${type.toString().lowercase()}"
    }
}
