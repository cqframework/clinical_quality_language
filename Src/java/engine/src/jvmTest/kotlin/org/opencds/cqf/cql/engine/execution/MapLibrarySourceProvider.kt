package org.opencds.cqf.cql.engine.execution

import java.io.ByteArrayInputStream
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier

/**
 * This class provides CQL content for a given VersionedIdentifier based on a pre-populated Map.
 * This is mostly useful for testing scenarios
 */
class MapLibrarySourceProvider(libraries: MutableMap<VersionedIdentifier?, String>?) :
    LibrarySourceProvider {
    private var libraries: MutableMap<VersionedIdentifier?, String>? = null

    init {
        this.libraries = libraries
    }

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source {
        val text: String = this.libraries!![libraryIdentifier]!!
        return ByteArrayInputStream(text.toByteArray()).asSource().buffered()
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
