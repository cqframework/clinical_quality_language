package org.opencds.cqf.cql.engine.elm.visiting;

import org.apache.commons.lang3.NotImplementedException;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.State;

public class RepeatEvaluator {

    public static Object repeat(Object source, Object element, String scope) {
        // TODO
        throw new NotImplementedException("Repeat operation not yet implemented");
    }

    public static Object internalEvaluate(Object source, Object element, String scope, State state) {
        return repeat(source, element, scope);
    }
}
