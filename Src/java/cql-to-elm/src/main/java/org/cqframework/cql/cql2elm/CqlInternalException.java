package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

/**
 * Created by Bryn on 5/20/2017.
 */
public class CqlInternalException extends CqlCompilerException {
    public CqlInternalException(String message) {
        super(message, ErrorSeverity.Error);
    }

    public CqlInternalException(String message, Throwable cause) {
        super(message, ErrorSeverity.Error, cause);
    }

    public CqlInternalException(String message, TrackBack locator) {
        super(message, ErrorSeverity.Error, locator);
    }

    public CqlInternalException(String message, TrackBack locator, Throwable cause) {
        super(message, ErrorSeverity.Error, locator, cause);
    }
}
