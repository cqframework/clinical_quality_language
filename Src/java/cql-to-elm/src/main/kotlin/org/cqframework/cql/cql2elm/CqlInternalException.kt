package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack

/** Created by Bryn on 5/20/2017. */
class CqlInternalException : CqlCompilerException {
    constructor(message: String?) : super(message, ErrorSeverity.Error)

    constructor(message: String?, cause: Throwable?) : super(message, ErrorSeverity.Error, cause)

    constructor(
        message: String?,
        locator: TrackBack?
    ) : super(message, ErrorSeverity.Error, locator)

    constructor(
        message: String?,
        locator: TrackBack?,
        cause: Throwable?
    ) : super(message, ErrorSeverity.Error, locator, cause)
}
