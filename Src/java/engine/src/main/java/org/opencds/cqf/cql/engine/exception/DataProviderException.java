package org.opencds.cqf.cql.engine.exception;

/*
 * This class is meant to be thrown by implementations of the DataProvider interface whenever they encounter an Exception
 */
public class DataProviderException extends CqlException {
    private static final long serialVersionUID = 1L;

    public DataProviderException(String message) {
        super(message);
    }
}
