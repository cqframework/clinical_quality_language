package org.opencds.cqf.cql.engine.exception;

public class InvalidConversion extends CqlException {
    private static final long serialVersionUID = 1L;

    public InvalidConversion(String message) {
        super(message);
    }

    public InvalidConversion(Object from, Object to) {
        super(String.format("Cannot Convert a value of type %s as %s.", from.getClass().getName(), to.getClass().getName()));
    }
}
