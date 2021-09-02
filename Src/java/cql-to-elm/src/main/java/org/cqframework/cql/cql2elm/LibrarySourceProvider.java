package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;

public interface LibrarySourceProvider {
    LibraryContentMeta getLibrarySource(VersionedIdentifier libraryIdentifier);
}
