package org.opencds.cqf.cql.engine.elm.executing;

import java.util.List;
import org.opencds.cqf.cql.engine.runtime.Code;

/*
structured type Concept
{
  codes List<Code>,
  display String
}

The Concept type represents a single terminological concept within CQL.
*/

public class ConceptEvaluator {

    public static Object internalEvaluate(List<Code> codes, String display) {

        return new org.opencds.cqf.cql.engine.runtime.Concept().withCodes(codes).withDisplay(display);
    }
}
