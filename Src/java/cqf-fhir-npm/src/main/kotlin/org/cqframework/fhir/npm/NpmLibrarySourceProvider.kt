package org.cqframework.fhir.npm

import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.plus
import kotlinx.io.Source
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.r5.context.ILoggingService
import org.hl7.fhir.utilities.npm.NpmPackage

/** Provides a library source provider that can resolve CQL library source from an Npm package */
class NpmLibrarySourceProvider(
    private val packages: MutableList<NpmPackage>,
    private val reader: ILibraryReader,
    private val logger: ILoggingService,
) : LibrarySourceProvider {
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        // VersionedIdentifier.id: Name of the library
        // VersionedIdentifier.system: Namespace for the library, as a URL
        // VersionedIdentifier.version: Version of the library

        for (p in packages) {
            try {
                val identifier =
                    VersionedIdentifier()
                        .withId(libraryIdentifier.id)
                        .withVersion(libraryIdentifier.version)
                        .withSystem(libraryIdentifier.system)

                if (identifier.system == null) {
                    identifier.system = p.canonical()
                }

                val s =
                    p.loadByCanonicalVersion(
                        identifier.system + "/Library/" + identifier.id,
                        identifier.version,
                    )
                if (s != null) {
                    val l = reader.readLibrary(s)
                    for (a in l!!.getContent()) {
                        if (a.getContentType() != null && a.getContentType() == "text/cql") {
                            if (identifier.system == null) {
                                identifier.system = libraryIdentifier.system
                            }
                            return ByteArrayInputStream(a.getData()).asSource().buffered()
                        }
                    }
                }
            } catch (e: IOException) {
                logger.logDebugMessage(
                    ILoggingService.LogCategory.PROGRESS,
                    "Exceptions occurred attempting to load npm library source for $libraryIdentifier",
                )
            }
        }

        return null
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
