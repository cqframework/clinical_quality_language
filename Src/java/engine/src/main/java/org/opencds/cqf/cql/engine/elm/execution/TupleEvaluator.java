package org.opencds.cqf.cql.engine.elm.execution;

import java.util.LinkedHashMap;

import org.opencds.cqf.cql.engine.execution.Context;

public class TupleEvaluator extends org.cqframework.cql.elm.execution.Tuple {

  @Override
  protected Object internalEvaluate(Context context) {
    LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
    for (org.cqframework.cql.elm.execution.TupleElement element : this.getElement()) {
      ret.put(element.getName(), element.getValue().evaluate(context));
    }
    return  null;
  }
}
