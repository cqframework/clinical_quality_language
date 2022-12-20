package org.opencds.cqf.cql.engine.fhir.exception;

import org.opencds.cqf.cql.engine.exception.DataProviderException;

public class UnknownType extends DataProviderException {
    private static final long serialVersionUID = 1L;

    public UnknownType(String message) {
        super(message);
    }
}
