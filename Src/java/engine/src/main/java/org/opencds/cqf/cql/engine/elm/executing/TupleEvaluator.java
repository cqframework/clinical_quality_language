package org.opencds.cqf.cql.engine.elm.executing;

import java.util.LinkedHashMap;
import org.opencds.cqf.cql.engine.execution.State;

public class TupleEvaluator {

    public static Object internalEvaluate(LinkedHashMap<String, Object> ret, State state) {
        return new org.opencds.cqf.cql.engine.runtime.Tuple(state).withElements(ret);
    }
}
