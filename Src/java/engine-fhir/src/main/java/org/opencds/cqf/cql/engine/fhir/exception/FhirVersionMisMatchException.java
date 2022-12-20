package org.opencds.cqf.cql.engine.fhir.exception;

public class FhirVersionMisMatchException extends RuntimeException {
    private static final long serialVersionUID = 01L;

    /* Constructor of custom FhirVersionMisMatchException class
     * @param message is the description of the exception
     */
    public FhirVersionMisMatchException(String message) {
        super(message);
    }
}
