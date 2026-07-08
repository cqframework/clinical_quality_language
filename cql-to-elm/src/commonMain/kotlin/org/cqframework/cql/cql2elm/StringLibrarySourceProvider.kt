package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import org.cqframework.cql.cql2elm.utils.asSource
import org.hl7.elm.r1.VersionedIdentifier

/**
 * This class implements the [LibrarySourceProvider] API, using a set of strings representing CQL
 * library content as a source.
 */
class StringLibrarySourceProvider(private val libraries: List<String>) : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val id: String = libraryIdentifier.id!!
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
        require(matches.size <= 1) {
            """"
                Multiple libraries for id : $libraryIdentifier resolved.
                Ensure that there are no duplicates in the input set."""
                .trimIndent()
        }
        return if (matches.size == 1) matches[0].asSource() else null
    }
}
