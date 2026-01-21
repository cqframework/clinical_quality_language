package org.opencds.cqf.cql.engine.execution

data class Variable(val name: String?) {

    var value: Any? = null

    // for AliasEvaluator
    var isList: Boolean = false

    fun withValue(value: Any?): Variable {
        this.value = value
        return this
    }
}
