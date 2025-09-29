package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.elm.r1.VersionedIdentifier

class TestLibrarySourceProvider(val path: String? = "LibraryTests") : LibrarySourceProvider {

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL)
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        val inputStream =
            TestLibrarySourceProvider::class
                .java
                .getResourceAsStream(getFileName(libraryIdentifier, type))
        return inputStream?.asSource()?.buffered()
    }

    private fun getFileName(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): String {
        val version = libraryIdentifier.version?.let { "-$it" } ?: ""
        return "$path/${libraryIdentifier.id}${version}.${type.toString().lowercase()}"
    }
}
