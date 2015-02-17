package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

public class CqlTranslatorException extends RuntimeException {
    public CqlTranslatorException(String message) {
        super(message);
    }

    public CqlTranslatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CqlTranslatorException(String message, TrackBack locator) {
        super(message);
        this.locator = locator;
    }

    public CqlTranslatorException(String message, TrackBack locator, Throwable cause) {
        super(message, cause);
        this.locator = locator;
    }

    private TrackBack locator;

    public TrackBack getLocator() { return locator; }
}
