package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack

/** Created by Bryn on 3/27/2017. */
class CqlSemanticException(
    message: String,
    locator: TrackBack? = null,
    severity: ErrorSeverity = ErrorSeverity.Error,
    cause: Throwable? = null
) : CqlCompilerException(message, locator, severity, cause)
