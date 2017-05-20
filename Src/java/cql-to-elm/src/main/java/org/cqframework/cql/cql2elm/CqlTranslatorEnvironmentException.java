package org.cqframework.cql.cql2elm;

/**
 * Created by Bryn on 5/20/2017.
 */
public class CqlTranslatorEnvironmentException extends RuntimeException {
    public CqlTranslatorEnvironmentException(String message) {
        super(message);
    }

    public CqlTranslatorEnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
