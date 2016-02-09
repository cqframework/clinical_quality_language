package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

public class CqlTranslatorIncludeException extends RuntimeException {
    private String libraryId;
    private String versionId;
    
    public CqlTranslatorIncludeException(String message, String libraryId, String versionId) {
        super(message);
        this.libraryId = libraryId;
        this.versionId = versionId;
    }

    public CqlTranslatorIncludeException(String message, String libraryId, String versionId, Throwable cause) {
        super(message, cause);
        this.libraryId = libraryId;
        this.versionId = versionId;
    }
    
    public String getLibraryId() {
        return libraryId;
    }

    public String getVersionId() {
        return versionId;
    }
}