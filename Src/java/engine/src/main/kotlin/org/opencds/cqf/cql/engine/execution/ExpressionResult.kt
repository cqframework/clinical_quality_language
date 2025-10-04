package org.opencds.cqf.cql.engine.execution

class ExpressionResult(
    protected var value: Any?,
    protected var evaluatedResources: MutableSet<Any?>?,
) {
    fun value(): Any? {
        return value
    }

    fun evaluatedResources(): MutableSet<Any?>? {
        return this.evaluatedResources
    }
}
