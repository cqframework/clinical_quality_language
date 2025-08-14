package org.cqframework.cql.cql2elm.preprocessor

import org.cqframework.cql.gen.cqlParser.ConceptDefinitionContext

/** Created by Bryn on 5/22/2016. */
class ConceptDefinitionInfo(val name: String, override val definition: ConceptDefinitionContext) :
    BaseInfo(definition)
