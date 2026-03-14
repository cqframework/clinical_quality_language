package org.opencds.cqf.cql.engine.exception

import org.opencds.cqf.cql.engine.debug.SourceLocator

/**
 * This class is meant to be thrown by implementations of the TerminologyProvider interface whenever
 * they encounter an Exception
 */
class TerminologyProviderException : CqlException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, sourceLocator: SourceLocator?) : super(message, sourceLocator)

    constructor(
        message: String?,
        cause: Throwable?,
        sourceLocator: SourceLocator?,
    ) : super(message, cause, sourceLocator)

    constructor(cause: Throwable?, sourceLocator: SourceLocator?) : super(cause, sourceLocator)

    constructor(
        message: String?,
        sourceLocator: SourceLocator?,
        severity: Severity?,
    ) : super(message, sourceLocator, severity)

    constructor(
        message: String?,
        cause: Throwable?,
        sourceLocator: SourceLocator?,
        severity: Severity?,
    ) : super(message, cause, sourceLocator, severity)

    companion object {
        private const val serialVersionUID = 1L
    }
}
