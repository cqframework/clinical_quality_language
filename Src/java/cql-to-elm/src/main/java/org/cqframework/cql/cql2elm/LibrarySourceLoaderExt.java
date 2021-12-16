package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;
import java.util.Set;

/**
 *
 * @author Nazmul
 */
public interface LibrarySourceLoaderExt extends LibrarySourceLoader {

    InputStream getLibrarySource(VersionedIdentifier libraryIdentifier, LibraryContentType type);

    boolean isLibrarySourceAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type);

    Set<LibraryContentType> getSupportedContentTypes();

    default InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibrarySource(libraryIdentifier, LibraryContentType.CQL);
    }

}