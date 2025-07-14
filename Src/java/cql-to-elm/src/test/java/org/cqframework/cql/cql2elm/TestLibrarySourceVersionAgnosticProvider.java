package org.cqframework.cql.cql2elm;

import java.io.InputStream;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * Clone of the {@link TestLibrarySourceProvider} that does not enforce versioning in the file names and thus will
 * support tests that do not specify a version in the library identifier.
 */
public class TestLibrarySourceVersionAgnosticProvider implements LibrarySourceProvider {

    private String path = "LibraryTests";

    public TestLibrarySourceVersionAgnosticProvider() {}

    public TestLibrarySourceVersionAgnosticProvider(String path) {
        this.path = path;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL);
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return TestLibrarySourceVersionAgnosticProvider.class.getResourceAsStream(getFileName(libraryIdentifier, type));
    }

    private String getFileName(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return String.format(
                "%s/%s.%s", path, libraryIdentifier.getId(), type.toString().toLowerCase());
    }
}
