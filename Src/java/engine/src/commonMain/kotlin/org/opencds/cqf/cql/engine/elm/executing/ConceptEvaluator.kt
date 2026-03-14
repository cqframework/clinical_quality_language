package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept

/*
structured type Concept
{
  codes List<Code>,
  display String
}

The Concept type represents a single terminological concept within CQL.
*/
object ConceptEvaluator {
    fun internalEvaluate(codes: List<Code?>?, display: String?): Any? {
        return Concept().withCodes(codes).withDisplay(display)
    }
}
