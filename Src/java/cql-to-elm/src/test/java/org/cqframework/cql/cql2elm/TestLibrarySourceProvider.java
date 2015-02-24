package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public class TestLibrarySourceProvider implements LibrarySourceProvider {
    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return TestLibrarySourceProvider.class.getResourceAsStream(String.format("LibraryTests/%s.cql",
                libraryIdentifier.getId()));
    }
}
