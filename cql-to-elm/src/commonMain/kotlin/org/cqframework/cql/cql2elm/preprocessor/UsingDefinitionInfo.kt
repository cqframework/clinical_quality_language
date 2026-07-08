package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.UsingDefinitionContext

class UsingDefinitionInfo(
    val namespaceName: String?,
    val name: String,
    val version: String?,
    val localName: String,
    override val definition: UsingDefinitionContext,
) : BaseInfo(definition)
