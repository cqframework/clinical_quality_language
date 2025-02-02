package org.opencds.cqf.cql.engine.fhir.data;

import java.io.InputStream;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

public class TestLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = String.format(
                "%s.cql",
                libraryIdentifier
                        .getId()); // , libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion())
        // : "");
        return TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
