package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ValuesetDefinitionContext

class ValuesetDefinitionInfo(val name: String, override val definition: ValuesetDefinitionContext) :
    BaseInfo(definition)
