package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack

/** Created by Bryn on 5/20/2017. */
class CqlInternalException(message: String?, locator: TrackBack? = null, cause: Throwable? = null) :
    CqlCompilerException(message, ErrorSeverity.Error, locator, cause)
