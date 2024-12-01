package org.cqframework.cql.cql2elm

import org.cqframework.cql.elm.tracking.TrackBack

/** Created by Bryn on 3/27/2017. */
class CqlSyntaxException : CqlCompilerException {
    constructor(message: String?) : super(message)

    constructor(message: String?, severity: ErrorSeverity) : super(message, severity)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(
        message: String?,
        severity: ErrorSeverity,
        cause: Throwable?
    ) : super(message, severity, cause)

    constructor(message: String?, locator: TrackBack?) : super(message, locator)

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
