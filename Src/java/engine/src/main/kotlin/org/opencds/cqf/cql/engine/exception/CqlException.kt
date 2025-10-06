package org.opencds.cqf.cql.engine.exception

import org.opencds.cqf.cql.engine.debug.SourceLocator

open class CqlException
@JvmOverloads
constructor(
    message: String?,
    cause: Throwable? = null,
    sourceLocator: SourceLocator? = null,
    severity: Severity? = null,
) : RuntimeException(message, cause) {
    constructor(cause: Throwable?) : this(null, cause, null)

    constructor(
        message: String?,
        sourceLocator: SourceLocator?,
    ) : this(message, null, sourceLocator)

    constructor(
        cause: Throwable?,
        sourceLocator: SourceLocator?,
    ) : this(null, cause, sourceLocator, null)

    constructor(
        message: String?,
        sourceLocator: SourceLocator?,
        severity: Severity?,
    ) : this(message, null, sourceLocator, severity)

    val severity: Severity

    @Transient var sourceLocator: SourceLocator? = null

    val backtrace: Backtrace = Backtrace()

    init {
        this.sourceLocator = sourceLocator
        this.severity = if (severity != null) severity else Severity.ERROR
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
