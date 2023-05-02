package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.execution.State;

import java.util.LinkedHashMap;

public class TupleEvaluator {

  public static Object internalEvaluate(LinkedHashMap<String, Object> ret, State state) {
    return new org.opencds.cqf.cql.engine.runtime.Tuple(state).withElements(ret);
  }
}
