package org.opencds.cqf.cql.engine.execution

class Variable(val name: String?) {

    var value: Any? = null

    // for AliasEvaluator
    var isList: Boolean = false

    fun withValue(value: Any?): Variable {
        this.value = value
        return this
    }

    override fun toString(): String {
        return String.format("Variable{name=%s, value=%s, isList=%s}", name, value, isList)
    }
}
