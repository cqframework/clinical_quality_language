package org.cqframework.cql.cql2elm;

import java.io.InputStream;

import org.hl7.elm.r1.VersionedIdentifier;

public class TestLibrarySourceProvider implements LibrarySourceProvider {

    private String path = "LibraryTests";

    public TestLibrarySourceProvider() {

    }

    public TestLibrarySourceProvider(String path) {
        this.path = path;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String pathString = path != null ? path + "/" : "";
        String identifierString = libraryIdentifier.getId();
        String versionString = libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "";
        String libraryFileName = String.format("%s%s%s.cql", pathString, identifierString, versionString);
        return TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
    }
}
