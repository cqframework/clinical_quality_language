package org.opencds.cqf.cql.engine.execution;

import java.io.InputStream;
import java.util.Optional;

import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

public class TestLibrarySourceProvider implements LibrarySourceProvider {

    private final String subfolder;

    public TestLibrarySourceProvider() {
        this(null);
    }

    public TestLibrarySourceProvider(String subfolder) {
        this.subfolder = subfolder;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryFileName = getCqlPath(libraryIdentifier);
        return TestLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
    }

    private String getCqlPath(VersionedIdentifier libraryIdentifier) {
        return String.format(
                "%s%s.cql",
                Optional.ofNullable(subfolder).map(nonNull -> nonNull + "/").orElse(""), libraryIdentifier.getId());
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
