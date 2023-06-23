package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.execution.State;

public class AliasRefEvaluator {
    public static Object internalEvaluate(String name, State state) {
        return state.resolveAlias(name);
    }
}
