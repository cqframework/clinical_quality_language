package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class CodesystemDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: cqlParser.CodesystemDefinitionContext? = null

    fun withName(value: String?): CodesystemDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: cqlParser.CodesystemDefinitionContext?): CodesystemDefinitionInfo {
        this.definition = value
        return this
    }
}
