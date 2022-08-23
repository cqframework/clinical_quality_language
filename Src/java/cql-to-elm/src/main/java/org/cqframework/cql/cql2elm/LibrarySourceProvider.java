package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public interface LibrarySourceProvider {
    InputStream getLibrarySource(VersionedIdentifier libraryIdentifier);

    default boolean isLibraryContentAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL != type) {
            return false;
        }

        return getLibrarySource(libraryIdentifier) != null;
    }

    default InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }

}
