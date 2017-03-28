package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

/**
 * Created by Bryn on 3/27/2017.
 */
public class CqlSemanticException extends CqlTranslatorException {
    public CqlSemanticException(String message) {
        super(message);
    }

    public CqlSemanticException(String message, Throwable cause) {
        super(message, cause);
    }

    public CqlSemanticException(String message, TrackBack locator) {
        super(message, locator);
    }

    public CqlSemanticException(String message, TrackBack locator, Throwable cause) {
        super(message, locator, cause);
    }
}
