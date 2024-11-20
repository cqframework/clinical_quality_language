package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser

/** Created by Bryn on 5/22/2016. */
class ConceptDefinitionInfo : BaseInfo() {
    var name: String? = null
    override var definition: cqlParser.ConceptDefinitionContext? = null

    fun withName(value: String?): ConceptDefinitionInfo {
        name = value
        return this
    }

    fun withDefinition(value: cqlParser.ConceptDefinitionContext?): ConceptDefinitionInfo {
        this.definition = value
        return this
    }
}
