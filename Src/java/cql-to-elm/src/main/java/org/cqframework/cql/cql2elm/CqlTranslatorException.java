package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

public class CqlTranslatorException extends RuntimeException {
    public enum ErrorSeverity {
        Info,
        Warning,
        Error
    }

    public CqlTranslatorException(String message) {
        super(message);
        this.severity = ErrorSeverity.Error;
    }

    public CqlTranslatorException(String message, ErrorSeverity severity) {
        super(message);
        this.severity = severity;
    }

    public CqlTranslatorException(String message, Throwable cause) {
        super(message, cause);
        this.severity = ErrorSeverity.Error;
    }

    public CqlTranslatorException(String message, ErrorSeverity severity, Throwable cause) {
        super(message, cause);
        this.severity = severity;
    }

    public CqlTranslatorException(String message, TrackBack locator) {
        super(message);
        this.severity = ErrorSeverity.Error;
        this.locator = locator;
    }

    public CqlTranslatorException(String message, ErrorSeverity severity, TrackBack locator) {
        super(message);
        this.severity = severity;
        this.locator = locator;
    }

    public CqlTranslatorException(String message, TrackBack locator, Throwable cause) {
        super(message, cause);
        this.severity = ErrorSeverity.Error;
        this.locator = locator;
    }

    public CqlTranslatorException(String message, ErrorSeverity severity, TrackBack locator, Throwable cause) {
        super(message, cause);
        this.severity = severity;
        this.locator = locator;
    }

    private ErrorSeverity severity;

    public ErrorSeverity getSeverity() {
        return severity;
    }

    private TrackBack locator;

    public TrackBack getLocator() {
        return locator;
    }
}
