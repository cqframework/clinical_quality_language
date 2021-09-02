package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.InputStream;


public class ElmXmlLibrarySourceProvider implements LibrarySourceProvider {

    public LibraryContentMeta getLibrarySource(VersionedIdentifier libraryIdentifier) {

        LibraryContentMeta contentMeta = new LibraryContentMeta(LibraryContentType.XML_ELM);
        String libraryFileName = String.format("LibraryTests/%s%s.xml",
                libraryIdentifier.getId(), libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : "");
        InputStream is = ElmXmlLibrarySourceProvider.class.getResourceAsStream(libraryFileName);
        contentMeta.setSource(is);
        return contentMeta;
    }

}

