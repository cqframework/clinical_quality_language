package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public class TestLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = String.format("%s%s.cql",
                libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
        return TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
    }
}