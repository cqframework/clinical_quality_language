package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import org.cqframework.cql.cql2elm.utils.asSource
import org.hl7.elm.r1.VersionedIdentifier

/**
 * A library source loader factory suitable for JS environments.
 *
 * @param getLibraryCql a callback that returns the CQL content of a library given its id, system,
 *   and version
 * @return an instance of [ILibrarySourceLoader]
 */
internal fun createLibrarySourceLoader(
    getLibraryCql: (id: String, system: String?, version: String?) -> String? = { _, _, _ -> null }
): ILibrarySourceLoader {
    return object : ILibrarySourceLoader {
        override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
            val cql =
                getLibraryCql(
                    libraryIdentifier.id!!,
                    libraryIdentifier.system,
                    libraryIdentifier.version
                )
            return cql?.asSource()
        }

        override fun getLibraryContent(
            libraryIdentifier: VersionedIdentifier,
            type: LibraryContentType
        ): Source? {
            // The current simple implementation for JS only supports requesting the library CQL
            // source through `getLibraryCql` and not compiled libraries that are expected to be
            // returned here.
            return null
        }
    }
}
