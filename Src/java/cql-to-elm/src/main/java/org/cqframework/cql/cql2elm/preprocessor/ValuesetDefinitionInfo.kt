package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class ValuesetDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: cqlParser.ValuesetDefinitionContext? = null

    fun withName(value: String?): ValuesetDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: cqlParser.ValuesetDefinitionContext?): ValuesetDefinitionInfo {
        this.definition = value
        return this
    }
}
