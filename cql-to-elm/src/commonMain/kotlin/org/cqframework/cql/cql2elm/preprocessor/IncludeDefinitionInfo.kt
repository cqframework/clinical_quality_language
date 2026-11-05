package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.IncludeDefinitionContext

class IncludeDefinitionInfo(
    val namespaceName: String?,
    val name: String,
    val version: String?,
    val localName: String,
    override val definition: IncludeDefinitionContext,
) : BaseInfo(definition)
