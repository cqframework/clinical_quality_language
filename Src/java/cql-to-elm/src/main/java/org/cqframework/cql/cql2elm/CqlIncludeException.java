package org.cqframework.cql.cql2elm;

public class CqlIncludeException extends RuntimeException {
    private String librarySystem;
    private String libraryId;
    private String versionId;

    public CqlIncludeException(String message, String librarySystem, String libraryId, String versionId) {
        super(message);
        this.librarySystem = librarySystem;
        this.libraryId = libraryId;
        this.versionId = versionId;
    }

    public CqlIncludeException(
            String message, String librarySystem, String libraryId, String versionId, Throwable cause) {
        super(message, cause);
        this.librarySystem = librarySystem;
        this.libraryId = libraryId;
        this.versionId = versionId;
    }

    public String getLibrarySystem() {
        return librarySystem;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public String getVersionId() {
        return versionId;
    }
}
