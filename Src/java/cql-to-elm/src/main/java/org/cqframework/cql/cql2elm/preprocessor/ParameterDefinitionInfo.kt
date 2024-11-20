package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ParameterDefinitionContext

class ParameterDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: ParameterDefinitionContext? = null

    fun withName(value: String?): ParameterDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: ParameterDefinitionContext?): ParameterDefinitionInfo {
        this.definition = value
        return this
    }
}
