package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.CodeDefinitionContext

/** Created by Bryn on 5/22/2016. */
class CodeDefinitionInfo(val name: String, override val definition: CodeDefinitionContext) :
    BaseInfo(definition)
