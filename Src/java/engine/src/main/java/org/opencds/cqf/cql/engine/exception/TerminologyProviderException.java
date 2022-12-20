package org.opencds.cqf.cql.engine.exception;

import org.opencds.cqf.cql.engine.debug.SourceLocator;
/**
 * This class is meant to be thrown by implementations of the TerminologyProvider interface whenever they encounter an Exception
 */
public class TerminologyProviderException extends CqlException {
    private static final long serialVersionUID = 1L;

    public TerminologyProviderException (String message)
    {
        super(message);
    }
    public TerminologyProviderException (String message, Throwable cause) {
        super(message, cause);
    }
    public TerminologyProviderException (Throwable cause) {
        super(cause);
    }

    public TerminologyProviderException (String message, SourceLocator sourceLocator) {
        super(message, sourceLocator);
    }

    public TerminologyProviderException (String message, Throwable cause, SourceLocator sourceLocator) {
        super(message, cause, sourceLocator);
    }

    public TerminologyProviderException (Throwable cause, SourceLocator sourceLocator) {
        super(cause, sourceLocator);
    }

    public TerminologyProviderException (String message, SourceLocator sourceLocator, Severity severity) {
        super(message, sourceLocator, severity);
    }

    public TerminologyProviderException (String message, Throwable cause, SourceLocator sourceLocator, Severity severity) {
        super(message, cause, sourceLocator, severity);
    }

}
