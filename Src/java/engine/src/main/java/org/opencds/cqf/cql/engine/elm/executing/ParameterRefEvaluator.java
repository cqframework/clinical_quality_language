package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.ParameterRef;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;

public class ParameterRefEvaluator {
    public static Object internalEvaluate(ParameterRef parameterRef, State state, CqlEngine visitor) {
        return state.resolveParameterRef(parameterRef.getLibraryName(), parameterRef.getName(), visitor);
    }
}
