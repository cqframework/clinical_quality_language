package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ParameterDefinitionContext

class ParameterDefinitionInfo(
    val name: String,
    override val definition: ParameterDefinitionContext
) : BaseInfo(definition)
