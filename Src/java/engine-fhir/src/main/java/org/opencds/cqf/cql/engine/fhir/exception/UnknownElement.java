package org.opencds.cqf.cql.engine.fhir.exception;

import org.opencds.cqf.cql.engine.exception.DataProviderException;

public class UnknownElement extends DataProviderException {
    private static final long serialVersionUID = 1L;

    public UnknownElement(String message) {
        super(message);
    }
}
