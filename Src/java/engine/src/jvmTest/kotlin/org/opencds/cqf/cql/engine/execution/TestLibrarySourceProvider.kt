package org.opencds.cqf.cql.engine.execution

import java.util.*
import java.util.function.Function
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier

class TestLibrarySourceProvider @JvmOverloads constructor(private val subfolder: String? = null) :
    LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val libraryFileName = getCqlPath(libraryIdentifier)
        val inputStream = TestLibrarySourceProvider::class.java.getResourceAsStream(libraryFileName)
        return inputStream?.asSource()?.buffered()
    }

    private fun getCqlPath(libraryIdentifier: VersionedIdentifier): String {
        return String.format(
            "%s%s.cql",
            Optional.ofNullable<String?>(subfolder)
                .map(Function { nonNull -> "$nonNull/" })
                .orElse(""),
            libraryIdentifier.id,
        )
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
