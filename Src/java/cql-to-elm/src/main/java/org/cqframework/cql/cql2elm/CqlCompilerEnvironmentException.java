package org.cqframework.cql.cql2elm;

/**
 * Created by Bryn on 5/20/2017.
 */
public class CqlCompilerEnvironmentException extends RuntimeException {
    public CqlCompilerEnvironmentException(String message) {
        super(message);
    }

    public CqlCompilerEnvironmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
