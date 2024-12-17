package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ContextDefinitionContext

class ContextDefinitionInfo(override val definition: ContextDefinitionContext) :
    BaseInfo(definition) {
    var context: String? = null
}
