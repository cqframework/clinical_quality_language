package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class IncludeDefinitionInfo : BaseInfo() {
    var namespaceName: String? = null
    var name: String? = null
    var version: String? = null
    var localName: String? = null

    fun withName(value: String?): IncludeDefinitionInfo {
        name = value
        return this
    }

    fun withVersion(value: String?): IncludeDefinitionInfo {
        version = value
        return this
    }

    fun withLocalName(value: String?): IncludeDefinitionInfo {
        localName = value
        return this
    }

    override var definition: cqlParser.IncludeDefinitionContext? = null

    fun withDefinition(value: cqlParser.IncludeDefinitionContext?): IncludeDefinitionInfo {
        this.definition = value
        return this
    }
}