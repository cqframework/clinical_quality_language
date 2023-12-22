package org.cqframework.cql.cql2elm;

import java.util.List;
import org.cqframework.cql.elm.tracking.TrackBack;

public class CqlCompilerException extends RuntimeException {
    public enum ErrorSeverity {
        Info,
        Warning,
        Error
    }

    public static boolean hasErrors(List<CqlCompilerException> exceptions) {
        for (CqlCompilerException exception : exceptions) {
            if (exception.getSeverity() == ErrorSeverity.Error) {
                return true;
            }
        }

        return false;
    }

    public CqlCompilerException(String message) {
        super(message);
        this.severity = ErrorSeverity.Error;
    }

    public CqlCompilerException(String message, ErrorSeverity severity) {
        super(message);
        this.severity = severity;
    }

    public CqlCompilerException(String message, Throwable cause) {
        super(message, cause);
        this.severity = ErrorSeverity.Error;
    }

    public CqlCompilerException(String message, ErrorSeverity severity, Throwable cause) {
        super(message, cause);
        this.severity = severity;
    }

    public CqlCompilerException(String message, TrackBack locator) {
        super(message);
        this.severity = ErrorSeverity.Error;
        this.locator = locator;
    }

    public CqlCompilerException(String message, ErrorSeverity severity, TrackBack locator) {
        super(message);
        this.severity = severity;
        this.locator = locator;
    }

    public CqlCompilerException(String message, TrackBack locator, Throwable cause) {
        super(message, cause);
        this.severity = ErrorSeverity.Error;
        this.locator = locator;
    }

    public CqlCompilerException(String message, ErrorSeverity severity, TrackBack locator, Throwable cause) {
        super(message, cause);
        this.severity = severity;
        this.locator = locator;
    }

    private final ErrorSeverity severity;

    public ErrorSeverity getSeverity() {
        return severity;
    }

    private TrackBack locator;

    public TrackBack getLocator() {
        return locator;
    }

    public void setLocator(TrackBack locator) {
        this.locator = locator;
    }
}
