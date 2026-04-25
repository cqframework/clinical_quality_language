package org.opencds.cqf.cql.engine.data

import org.opencds.cqf.cql.engine.runtime.CqlType

interface ExternalFunctionProvider {
    fun evaluate(staticFunctionName: String?, arguments: MutableList<CqlType?>?): CqlType?
}
