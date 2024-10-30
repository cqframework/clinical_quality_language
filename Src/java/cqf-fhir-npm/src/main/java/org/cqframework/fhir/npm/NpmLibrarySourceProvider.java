package org.cqframework.fhir.npm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.fhir.r5.context.ILoggingService;
import org.hl7.fhir.r5.model.Library;
import org.hl7.fhir.utilities.npm.NpmPackage;

/**
 * Provides a library source provider that can resolve CQL library source from an Npm package
 */
public class NpmLibrarySourceProvider implements LibrarySourceProvider {

    public NpmLibrarySourceProvider(List<NpmPackage> packages, ILibraryReader reader, ILoggingService logger) {
        this.packages = packages;
        this.reader = reader;
        this.logger = logger;
    }

    private List<NpmPackage> packages;
    private ILibraryReader reader;
    private ILoggingService logger;

    @Override
    public InputStream getLibrarySource(VersionedIdentifier identifier) {
        // VersionedIdentifier.id: Name of the library
        // VersionedIdentifier.system: Namespace for the library, as a URL
        // VersionedIdentifier.version: Version of the library

        for (NpmPackage p : packages) {
            try {
                VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                        .withId(identifier.getId())
                        .withVersion(identifier.getVersion())
                        .withSystem(identifier.getSystem());

                if (libraryIdentifier.getSystem() == null) {
                    libraryIdentifier.setSystem(p.canonical());
                }

                InputStream s = p.loadByCanonicalVersion(
                        libraryIdentifier.getSystem() + "/Library/" + libraryIdentifier.getId(),
                        libraryIdentifier.getVersion());
                if (s != null) {
                    Library l = reader.readLibrary(s);
                    for (org.hl7.fhir.r5.model.Attachment a : l.getContent()) {
                        if (a.getContentType() != null && a.getContentType().equals("text/cql")) {
                            if (identifier.getSystem() == null) {
                                identifier.setSystem(libraryIdentifier.getSystem());
                            }
                            return new ByteArrayInputStream(a.getData());
                        }
                    }
                }
            } catch (IOException e) {
                logger.logDebugMessage(
                        ILoggingService.LogCategory.PROGRESS,
                        String.format(
                                "Exceptions occurred attempting to load npm library source for %s",
                                identifier.toString()));
            }
        }

        return null;
    }
}
