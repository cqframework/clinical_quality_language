package org.cqframework.cql.cql2elm;

import kotlinx.io.Source;
import org.hl7.elm.r1.VersionedIdentifier;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

public class TestLibrarySourceProvider implements LibrarySourceProvider {

    private String path = "LibraryTests";

    public TestLibrarySourceProvider() {}

    public TestLibrarySourceProvider(String path) {
        this.path = path;
    }

    @Override
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibraryContent(libraryIdentifier, LibraryContentType.CQL);
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        var inputStream = TestLibrarySourceProvider.class.getResourceAsStream(getFileName(libraryIdentifier, type));
        return inputStream == null ? null : buffered(asSource(inputStream));
    }

    private String getFileName(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        return String.format(
                "%s/%s%s.%s",
                path,
                libraryIdentifier.getId(),
                libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "",
                type.toString().toLowerCase());
    }
}
