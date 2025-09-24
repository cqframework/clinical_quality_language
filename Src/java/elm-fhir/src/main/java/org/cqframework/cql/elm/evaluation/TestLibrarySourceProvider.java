package org.cqframework.cql.elm.evaluation;

import kotlinx.io.Source;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

public class TestLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = String.format(
                "%s.cql",
                libraryIdentifier
                        .getId()); // , libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion())
        // : "");
        var inputStream = TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
        return inputStream == null ? null : buffered(asSource(inputStream));
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
