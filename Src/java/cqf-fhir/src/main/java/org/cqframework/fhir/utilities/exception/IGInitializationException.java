package org.cqframework.fhir.utilities.exception;

/**
 * This exception is thrown whenever there is an issue initializing the tooling from a source IG.
 */
public class IGInitializationException extends RuntimeException {
    static final long serialVersionUID = 1L;

    public IGInitializationException() {
        super();
    }

    public IGInitializationException(String message) {
        super(message);
    }

    public IGInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IGInitializationException(Throwable cause) {
        super(cause);
    }
}
