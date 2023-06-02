package org.opencds.cqf.cql.engine.exception;

public class InvalidDateTime extends CqlException {
    private static final long serialVersionUID = 1L;

    public InvalidDateTime(String message) {
        super(message);
    }

    public InvalidDateTime(String message, Throwable cause) {
        super(message, cause);
    }
}
