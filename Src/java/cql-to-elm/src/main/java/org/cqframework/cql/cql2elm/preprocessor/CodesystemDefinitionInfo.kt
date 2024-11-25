package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.CodesystemDefinitionContext

class CodesystemDefinitionInfo(
    val name: String,
    override val definition: CodesystemDefinitionContext
) : BaseInfo(definition)
