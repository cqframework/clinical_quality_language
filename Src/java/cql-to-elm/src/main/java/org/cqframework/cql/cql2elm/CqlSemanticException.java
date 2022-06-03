package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

/**
 * Created by Bryn on 3/27/2017.
 */
public class CqlSemanticException extends CqlCompilerException {
    public CqlSemanticException(String message) {
        super(message);
    }

    public CqlSemanticException(String message, ErrorSeverity severity) {
        super(message, severity);
    }

    public CqlSemanticException(String message, Throwable cause) {
        super(message, cause);
    }

    public CqlSemanticException(String message, ErrorSeverity severity, Throwable cause) {
        super(message, severity, cause);
    }

    public CqlSemanticException(String message, TrackBack locator) {
        super(message, locator);
    }

    public CqlSemanticException(String message, ErrorSeverity severity, TrackBack locator) {
        super(message, severity, locator);
    }

    public CqlSemanticException(String message, TrackBack locator, Throwable cause) {
        super(message, locator, cause);
    }

    public CqlSemanticException(String message, ErrorSeverity severity, TrackBack locator, Throwable cause) {
        super(message, severity, locator, cause);
    }
}
