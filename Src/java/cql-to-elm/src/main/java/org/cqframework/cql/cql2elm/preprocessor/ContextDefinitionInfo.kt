package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

class ContextDefinitionInfo : BaseInfo() {
    var context: String? = null
    override var definition: cqlParser.ContextDefinitionContext? = null
}
