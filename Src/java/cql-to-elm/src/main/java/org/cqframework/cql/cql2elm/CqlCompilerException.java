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
        this(message, ErrorSeverity.Error, null, null);
    }

    public CqlCompilerException(String message, ErrorSeverity severity) {
        this(message, severity, null, null);
    }

    public CqlCompilerException(String message, Throwable cause) {
        this(message, ErrorSeverity.Error, null, cause);
    }

    public CqlCompilerException(String message, ErrorSeverity severity, Throwable cause) {
        this(message, severity, null, cause);
    }

    public CqlCompilerException(String message, TrackBack locator) {
        this(message, ErrorSeverity.Error, locator, null);
    }

    public CqlCompilerException(String message, ErrorSeverity severity, TrackBack locator) {
        this(message, severity, locator, null);
    }

    public CqlCompilerException(String message, TrackBack locator, Throwable cause) {
        this(message, ErrorSeverity.Error, locator, cause);
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

    private final transient TrackBack locator;

    public TrackBack getLocator() {
        return locator;
    }
}
