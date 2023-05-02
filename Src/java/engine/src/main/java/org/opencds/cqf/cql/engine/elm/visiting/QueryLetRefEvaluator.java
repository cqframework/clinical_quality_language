package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.QueryLetRef;
import org.opencds.cqf.cql.engine.execution.State;

public class QueryLetRefEvaluator {
    public static Object internalEvaluate(QueryLetRef elm, State state) {
        return state.resolveVariable(elm.getName()).getValue();
    }
}
