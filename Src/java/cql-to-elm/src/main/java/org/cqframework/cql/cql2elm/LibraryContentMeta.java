package org.cqframework.cql.cql2elm;

import java.io.InputStream;

public class LibraryContentMeta {

    private LibraryContentType libraryContentType;
    private InputStream source;

    public  LibraryContentMeta () {
    }

    public LibraryContentMeta(LibraryContentType type) {
        libraryContentType = type;
    }

    public LibraryContentType getLibraryContentType() {
        return libraryContentType;
    }

    public void setLibraryContentType(LibraryContentType libraryContentType) {
        this.libraryContentType = libraryContentType;
    }

    public InputStream getSource() {
        return source;
    }

    public void setSource(InputStream source) {
        this.source = source;
    }

    public LibraryContentMeta withSource(InputStream source) {
        this.source = source;
        return this;
    }
}
