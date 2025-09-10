@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlinx.io.Source
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm.r1.VersionedIdentifier

@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
interface LibrarySourceProvider {
    fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source?

    fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): Source? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }
        return null
    }
}

expect fun getLibrarySourceProviders(refresh: Boolean): Iterator<LibrarySourceProvider>

@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
fun createLibrarySourceProvider(
    getLibraryCql: (id: String, system: String?, version: String?) -> Source?,
): LibrarySourceProvider {
    return object : LibrarySourceProvider {
        override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
            return getLibraryCql(
                libraryIdentifier.id!!,
                libraryIdentifier.system,
                libraryIdentifier.version
            )
        }
    }
}
