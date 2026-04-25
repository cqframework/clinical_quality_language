package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
structured type Concept
{
  codes List<Code>,
  display String
}

The Concept type represents a single terminological concept within CQL.
*/
object ConceptEvaluator {
    // codes: List<Code?>?
    fun internalEvaluate(codes: CqlType?, display: kotlin.String?): Concept {
        if (codes == null) {
            return Concept().withDisplay(display)
        }

        if (codes is List && codes.all { it is Code? }) {
            return Concept().withCodes(codes.filterIsInstance<Code?>()).withDisplay(display)
        }

        throw InvalidOperatorArgument("Expected List<Code>, found $codes.")
    }
}
