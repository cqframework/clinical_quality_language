package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.execution.State;

public class IdentifierRefEvaluator {

    public static Object internalEvaluate(String name, State state) {

        if (name == null) {
            return null;
        }

        return state.resolveIdentifierRef(name);
    }
}
