package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;


public class ElmJsonLibrarySourceProvider implements LibrarySourceProvider {

    public LibraryContentMeta getLibrarySource(VersionedIdentifier libraryIdentifier) {

        LibraryContentMeta contentMeta = new LibraryContentMeta(LibraryContentType.JSON_ELM);
        String libraryFileName = String.format("LibraryTests/%s%s.json",
                libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
        InputStream is = ElmJsonLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
        contentMeta.setSource(is);
        return contentMeta;
    }

}

