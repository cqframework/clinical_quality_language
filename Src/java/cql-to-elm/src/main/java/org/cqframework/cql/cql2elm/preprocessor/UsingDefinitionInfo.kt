package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.UsingDefinitionContext

class UsingDefinitionInfo : BaseInfo() {
    var namespaceName: String? = null
    @JvmField var name: String? = null
    @JvmField var version: String? = null
    var localName: String? = null

    fun withNamespaceName(value: String?): UsingDefinitionInfo {
        namespaceName = value
        return this
    }

    fun withName(value: String?): UsingDefinitionInfo {
        name = value
        return this
    }

    fun withVersion(value: String?): UsingDefinitionInfo {
        version = value
        return this
    }

    fun withLocalName(value: String?): UsingDefinitionInfo {
        localName = value
        return this
    }

    override var definition: UsingDefinitionContext? = null

    fun withDefinition(value: UsingDefinitionContext?): UsingDefinitionInfo {
        this.definition = value
        return this
    }
}
