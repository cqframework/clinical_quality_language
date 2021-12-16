package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;


public interface LibrarySourceProviderExt extends LibrarySourceProvider {

    boolean isLibrarySourceAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type);

    LibraryContentType getLibrarySourceType();

    InputStream getLibrarySource(VersionedIdentifier libraryIdentifier, LibraryContentType type);

    default InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibrarySource(libraryIdentifier, LibraryContentType.CQL);
    }
}