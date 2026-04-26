package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.runtime.Value

data class Variable(val name: String?) {

    var value: Value? = null

    // for AliasEvaluator
    var isList: Boolean = false

    fun withValue(value: Value?): Variable {
        this.value = value
        return this
    }
}
