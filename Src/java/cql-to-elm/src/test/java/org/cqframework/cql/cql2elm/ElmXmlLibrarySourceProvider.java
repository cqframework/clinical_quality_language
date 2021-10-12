package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;


public class ElmXmlLibrarySourceProvider implements LibrarySourceProviderExt {

    @Override
    public boolean isLibrarySourceAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (!type.equals(LibraryContentType.ANY) && !type.equals(getLibrarySourceType())) {
            return false;
        }
        return ElmXmlLibrarySourceProvider.class.getResource(getFileName(libraryIdentifier)) != null;
    }

    @Override
    public LibraryContentType getLibrarySourceType() {
        return LibraryContentType.XML;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (!type.equals(LibraryContentType.ANY) && !type.equals(getLibrarySourceType())) {
            return null;
        }
        InputStream is = ElmXmlLibrarySourceProvider.class.getResourceAsStream(getFileName(libraryIdentifier));
        return is;
    }

    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        return getLibrarySource(libraryIdentifier, LibraryContentType.CQL);

    }

    private String getFileName(VersionedIdentifier libraryIdentifier) {
        return String.format("LibraryTests/%s%s.xml",
                libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
    }

}

