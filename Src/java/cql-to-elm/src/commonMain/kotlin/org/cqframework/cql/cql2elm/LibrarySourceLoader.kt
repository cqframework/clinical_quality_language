package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlinx.io.Source
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm.r1.VersionedIdentifier

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
interface LibrarySourceLoader {
    fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source?

    fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }
        return null
    }

    fun clearProviders()

    fun registerProvider(provider: LibrarySourceProvider)
}
