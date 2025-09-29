package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ExpressionDefinitionContext

class ExpressionDefinitionInfo(
    val name: String,
    val context: String,
    override val definition: ExpressionDefinitionContext?,
) : BaseInfo(definition)
