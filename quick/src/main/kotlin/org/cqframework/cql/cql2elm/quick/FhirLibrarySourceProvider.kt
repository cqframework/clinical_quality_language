package org.cqframework.cql.cql2elm.quick

import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceInfo
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier

/** Created by Bryn on 3/28/2017. */
class FhirLibrarySourceProvider : LibrarySourceProvider, NamespaceAware {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val result =
            FhirLibrarySourceProvider::class
                .java
                .getResourceAsStream(
                    "/org/hl7/fhir/${libraryIdentifier.id}-${libraryIdentifier.version}.cql"
                )

        if (result != null && namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            // If the context already has a namespace registered for FHIR, use that.
            var namespaceInfo = namespaceManager!!.getNamespaceInfoFromUri(NAMESPACE_URI)
            if (namespaceInfo == null) {
                namespaceInfo = NamespaceInfo(NAMESPACE_NAME, NAMESPACE_URI)
                namespaceManager!!.ensureNamespaceRegistered(namespaceInfo)
            }
            libraryIdentifier.system = NAMESPACE_URI
        }

        return result?.asSource()?.buffered()
    }

    private var namespaceManager: NamespaceManager? = null

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
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

    companion object {
        private const val NAMESPACE_NAME = "FHIR"
        private const val NAMESPACE_URI = "http://hl7.org/fhir"
    }
}
