package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

/** Created by Bryn on 5/22/2016. */
class CodeDefinitionInfo : BaseInfo() {
    var name: String? = null

    fun withName(value: String?): CodeDefinitionInfo {
        name = value
        return this
    }

    override var definition: cqlParser.CodeDefinitionContext? = null

    fun withDefinition(value: cqlParser.CodeDefinitionContext?): CodeDefinitionInfo {
        this.definition = value
        return this
    }
}
