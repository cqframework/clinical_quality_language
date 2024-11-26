package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.FunctionDefinitionContext

data class FunctionDefinitionInfo(
    val name: String,
    val context: String,
    override val definition: FunctionDefinitionContext
) : BaseInfo(definition)
