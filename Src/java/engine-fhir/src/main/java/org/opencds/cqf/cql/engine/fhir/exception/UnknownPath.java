package org.opencds.cqf.cql.engine.fhir.exception;

import org.opencds.cqf.cql.engine.exception.DataProviderException;

public class UnknownPath extends DataProviderException {
    private static final long serialVersionUID = 1L;

    public UnknownPath(String message) {
        super(message);
    }
}
