package org.opencds.cqf.cql.engine.execution

import org.opencds.cqf.cql.engine.runtime.CqlType

data class Variable(val name: String?) {

    var value: CqlType? = null

    // for AliasEvaluator
    var isList: Boolean = false

    fun withValue(value: CqlType?): Variable {
        this.value = value
        return this
    }
}
