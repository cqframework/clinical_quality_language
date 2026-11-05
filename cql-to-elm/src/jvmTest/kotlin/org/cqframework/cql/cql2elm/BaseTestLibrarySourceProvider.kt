package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.utils.asSource
import org.cqframework.cql.cql2elm.utils.getTranslatorVersion
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Loads libraries from the resources directory and replaces `{translatorVersion}` with the current
 * translator version in the library source to pass the compatibility checks.
 */
open class BaseTestLibrarySourceProvider(
    val getFileName: (libraryIdentifier: VersionedIdentifier, type: LibraryContentType) -> String
) : LibrarySourceProvider {

    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL)
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType,
    ): Source? {
        val inputStream =
            BaseTestLibrarySourceProvider::class
                .java
                .getResourceAsStream(getFileName(libraryIdentifier, type))
        if (inputStream != null) {
            val content = inputStream.readBytes().toString(Charsets.UTF_8)
            inputStream.close()
            val contentFixed = content.replace("{translatorVersion}", getTranslatorVersion())
            return contentFixed.asSource().buffered()
        }
        return null
    }
}
