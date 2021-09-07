package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public class TestLibrarySourceProvider implements LibrarySourceProvider {

    private String path = "LibraryTests";

    public TestLibrarySourceProvider() {

    }

    public TestLibrarySourceProvider(String path) {
        this.path = path;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = String.format("%s/%s%s.cql",
                path, libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
        return TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
    }
}
