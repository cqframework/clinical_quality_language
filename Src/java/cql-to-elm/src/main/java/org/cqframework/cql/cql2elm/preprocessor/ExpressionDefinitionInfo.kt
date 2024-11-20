package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class ExpressionDefinitionInfo : BaseInfo() {
    @JvmField var name: String? = null
    @JvmField var context: String? = null
    override var definition: cqlParser.ExpressionDefinitionContext? = null

    fun withName(value: String?): ExpressionDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: cqlParser.ExpressionDefinitionContext?): ExpressionDefinitionInfo {
        this.definition = value
        return this
    }
}
