package org.cqframework.cql.cql2elm;

import java.io.InputStream;
import org.hl7.elm.r1.VersionedIdentifier;

public interface LibrarySourceProvider {
    InputStream getLibrarySource(VersionedIdentifier libraryIdentifier);

    default InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
