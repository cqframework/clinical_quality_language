package org.opencds.cqf.cql.engine.execution;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.cqframework.cql.cql2elm.LibrarySourceProvider;

public class InMemoryLibrarySourceProvider implements LibrarySourceProvider {

    private Map<org.hl7.elm.r1.VersionedIdentifier, String> libraries = null;

    public InMemoryLibrarySourceProvider(Map<org.hl7.elm.r1.VersionedIdentifier, String> libraries) {
        this.libraries = libraries;
    }

    @Override
    public InputStream getLibrarySource(org.hl7.elm.r1.VersionedIdentifier libraryIdentifier) {
        String text = this.libraries.get(libraryIdentifier);
        return new ByteArrayInputStream(text.getBytes());
    }
}