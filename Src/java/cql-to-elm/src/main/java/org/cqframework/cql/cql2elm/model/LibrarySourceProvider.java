package org.cqframework.cql.cql2elm.model;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public interface LibrarySourceProvider {
    InputStream getLibrarySource(VersionedIdentifier libraryIdentifier);
}
