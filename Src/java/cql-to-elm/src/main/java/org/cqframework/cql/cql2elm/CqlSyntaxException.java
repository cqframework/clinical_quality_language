package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

/**
 * Created by Bryn on 3/27/2017.
 */
public class CqlSyntaxException extends CqlCompilerException {
    public CqlSyntaxException(String message) {
        super(message);
    }

    public CqlSyntaxException(String message, ErrorSeverity severity) {
        super(message, severity);
    }

    public CqlSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public CqlSyntaxException(String message, ErrorSeverity severity, Throwable cause) {
        super(message, severity, cause);
    }

    public CqlSyntaxException(String message, TrackBack locator) {
        super(message, locator);
    }

    public CqlSyntaxException(String message, ErrorSeverity severity, TrackBack locator) {
        super(message, severity, locator);
    }

    public CqlSyntaxException(String message, TrackBack locator, Throwable cause) {
        super(message, locator, cause);
    }

    public CqlSyntaxException(String message, ErrorSeverity severity, TrackBack locator, Throwable cause) {
        super(message, severity, locator, cause);
    }
}
