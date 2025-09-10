package org.cqframework.cql.cql2elm;


import kotlinx.io.Source;
import org.hl7.elm.r1.VersionedIdentifier;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

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
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL);
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        var inputStream = TestLibrarySourceVersionAgnosticProvider.class.getResourceAsStream(getFileName(libraryIdentifier, type));
        return inputStream == null ? null : buffered(asSource(inputStream));
    }

    private String getFileName(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return String.format(
                "%s/%s.%s", path, libraryIdentifier.getId(), type.toString().toLowerCase());
    }
}
