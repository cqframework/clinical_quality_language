package org.opencds.cqf.cql.engine.data

interface ExternalFunctionProvider {
    fun evaluate(staticFunctionName: String?, arguments: MutableList<Any?>?): Any?
}
