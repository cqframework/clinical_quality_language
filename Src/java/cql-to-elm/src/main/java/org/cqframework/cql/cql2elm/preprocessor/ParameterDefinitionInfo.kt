package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class ParameterDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: cqlParser.ParameterDefinitionContext? = null

    fun withName(value: String?): ParameterDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: cqlParser.ParameterDefinitionContext?): ParameterDefinitionInfo {
        this.definition = value
        return this
    }
}
