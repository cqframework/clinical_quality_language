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
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL);
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return TestLibrarySourceProvider.class.getResourceAsStream(getFileName(libraryIdentifier, type));
    }

    private String getFileName(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return String.format("%s/%s%s.%s",
                path, libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "", type.toString().toLowerCase());
    }
}
