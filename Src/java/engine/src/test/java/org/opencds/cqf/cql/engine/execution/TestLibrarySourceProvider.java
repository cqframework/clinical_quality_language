package org.opencds.cqf.cql.engine.execution;

import java.util.Optional;

import kotlinx.io.Source;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

public class TestLibrarySourceProvider implements LibrarySourceProvider {

    private final String subfolder;

    public TestLibrarySourceProvider() {
        this(null);
    }

    public TestLibrarySourceProvider(String subfolder) {
        this.subfolder = subfolder;
    }

    @Override
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = getCqlPath(libraryIdentifier);
        var inputStream = TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
        return inputStream == null ? null : buffered(asSource(inputStream));
    }

    private String getCqlPath(VersionedIdentifier libraryIdentifier) {
        return String.format(
                "%s%s.cql",
                Optional.ofNullable(subfolder).map(nonNull -> nonNull + "/").orElse(""), libraryIdentifier.getId());
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
