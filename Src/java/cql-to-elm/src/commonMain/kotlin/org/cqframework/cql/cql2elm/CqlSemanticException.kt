package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack

/** Created by Bryn on 3/27/2017. */
class CqlSemanticException : CqlCompilerException {
    constructor(message: String?) : super(message)

    constructor(
        message: String?,
        severity: ErrorSeverity,
        locator: TrackBack?
    ) : super(message, severity, locator)

    constructor(
        message: String?,
        locator: TrackBack?,
        cause: Throwable?
    ) : super(message, locator, cause)

    constructor(
        message: String?,
        severity: ErrorSeverity,
        locator: TrackBack?,
        cause: Throwable?
    ) : super(message, severity, locator, cause)
}
