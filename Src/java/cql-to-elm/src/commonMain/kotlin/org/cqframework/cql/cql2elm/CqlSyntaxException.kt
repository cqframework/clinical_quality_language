package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.tracking.TrackBack

/** Created by Bryn on 3/27/2017. */
class CqlSyntaxException(message: String, locator: TrackBack?, cause: Throwable?) :
    CqlCompilerException(message, locator, ErrorSeverity.Error, cause)
