package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Code;

/*
structured type Concept
{
  codes List<Code>,
  display String
}

The Concept type represents a single terminological concept within CQL.
*/

public class ConceptEvaluator extends org.cqframework.cql.elm.execution.Concept {

  @Override
  protected Object internalEvaluate(Context context) {
    ArrayList<Code> codes = new ArrayList<>();
    for (int i = 0; i < this.getCode().size(); ++i) {
      codes.add((Code)this.getCode().get(i).evaluate(context));
    }
    String display = this.getDisplay();
    return new org.opencds.cqf.cql.engine.runtime.Concept().withCodes(codes).withDisplay(display);
  }
}
