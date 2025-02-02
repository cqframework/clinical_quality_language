package org.opencds.cqf.cql.engine.fhir.data;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import kotlinx.io.Source;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

public class TestLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = String.format(
                "%s.cql",
                libraryIdentifier
                        .getId()); // , libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion())
        // : "");
        var inputStream = TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
        if (inputStream == null) {
            return null;
        }
        return buffered(asSource(inputStream));
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
