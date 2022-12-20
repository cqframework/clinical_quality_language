package org.opencds.cqf.cql.engine.exception;

import org.opencds.cqf.cql.engine.debug.SourceLocator;

public class CqlException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CqlException(String message) {
        this(message, null, null, null);
    }

    public CqlException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public CqlException(Throwable cause) {
        this(null, cause, null);
    }

    public CqlException(String message, SourceLocator sourceLocator) {
        this(message, null, sourceLocator);
    }

    public CqlException(String message, Throwable cause, SourceLocator sourceLocator) {
        this(message, cause, sourceLocator, null);
    }

    public CqlException(Throwable cause, SourceLocator sourceLocator) {
        super(cause);
        this.sourceLocator = sourceLocator;
        this.severity = Severity.ERROR;
    }

    public CqlException(String message, SourceLocator sourceLocator, Severity severity) {
        super(message);
        this.sourceLocator = sourceLocator;
        this.severity = severity != null ? severity : Severity.ERROR;
    }

    public CqlException(String message, Throwable cause, SourceLocator sourceLocator, Severity severity) {
        super(message, cause);
        this.sourceLocator = sourceLocator;
        this.severity = severity != null ? severity : Severity.ERROR;
    }

    private final Severity severity;

    public Severity getSeverity() {
        return severity;
    }

    private transient SourceLocator sourceLocator = null;

    public SourceLocator getSourceLocator() {
        return sourceLocator;
    }

    public void setSourceLocator(SourceLocator sourceLocator) {
        this.sourceLocator = sourceLocator;
    }
}
