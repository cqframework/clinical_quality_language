package org.opencds.cqf.cql.engine.data

import org.opencds.cqf.cql.engine.runtime.Value

interface ExternalFunctionProvider {
    fun evaluate(staticFunctionName: String?, arguments: MutableList<Value?>?): Value?
}
