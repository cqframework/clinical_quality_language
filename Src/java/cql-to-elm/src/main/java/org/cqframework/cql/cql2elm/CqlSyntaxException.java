package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;

/**
 * Created by Bryn on 3/27/2017.
 */
public class CqlSyntaxException extends CqlTranslatorException {
    public CqlSyntaxException(String message) {
        super(message);
    }

    public CqlSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public CqlSyntaxException(String message, TrackBack locator) {
        super(message, locator);
    }

    public CqlSyntaxException(String message, TrackBack locator, Throwable cause) {
        super(message, locator, cause);
    }
}
