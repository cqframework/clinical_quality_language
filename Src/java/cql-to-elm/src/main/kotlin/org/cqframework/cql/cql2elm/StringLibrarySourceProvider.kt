package org.cqframework.cql.cql2elm

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import org.hl7.elm.r1.VersionedIdentifier

/**
 * This class implements the LibrarySourceProvider API, using a set of strings representing CQL
 * library content as a source.
 */
class StringLibrarySourceProvider(private val libraries: List<String>) : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): InputStream? {
        val id: String = libraryIdentifier.id
        val version: String? = libraryIdentifier.version
        val maybeQuotedIdPattern = "(\"$id\"|$id)"
        var matchText = "(?s).*library\\s+\"?$maybeQuotedIdPattern"
        matchText +=
            if (version != null) {
                ("\\s+version\\s+'$version'\\s+(?s).*")
            } else {
                "\\s+(?s).*"
            }
        val matches: ArrayList<String> = ArrayList()
        for (library: String in libraries) {
            if (library.matches(matchText.toRegex())) {
                matches.add(library)
            }
        }
        if (matches.size > 1) {
            throw IllegalArgumentException(
                String.format(
                    Locale.US,
                    "Multiple libraries for id : %s resolved.%nEnsure that there are no duplicates in the input set.",
                    libraryIdentifier.toString()
                )
            )
        }
        return if (matches.size == 1) ByteArrayInputStream(matches[0].toByteArray()) else null
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): InputStream? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }

        return null
    }
}
