package org.cqframework.cql.cql2elm;

/**
 * This enum lists all the encodings for CQL libraries
 */
public enum LibraryContentType implements MimeType {
    CQL("text/cql"),
    XML("application/elm+xml"),
    JSON("application/elm+json"),
    COFFEE("application/elm+coffee");

    private LibraryContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    private final String mimeType;

    @Override
    public String mimeType() {
        return mimeType;
    }
}
