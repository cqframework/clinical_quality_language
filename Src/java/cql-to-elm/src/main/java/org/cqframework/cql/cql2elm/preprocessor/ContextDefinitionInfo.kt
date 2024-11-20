package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ContextDefinitionContext

class ContextDefinitionInfo : BaseInfo() {
    var context: String? = null
    override var definition: ContextDefinitionContext? = null
}
