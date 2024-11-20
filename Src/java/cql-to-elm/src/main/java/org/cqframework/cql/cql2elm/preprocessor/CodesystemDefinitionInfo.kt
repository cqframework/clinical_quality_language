package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.CodesystemDefinitionContext

class CodesystemDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: CodesystemDefinitionContext? = null

    fun withName(value: String?): CodesystemDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: CodesystemDefinitionContext?): CodesystemDefinitionInfo {
        this.definition = value
        return this
    }
}
