package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ParameterRef;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;

public class ParameterRefEvaluator {
    public static Object internalEvaluate(ParameterRef parameterRef, State state, CqlEngineVisitor visitor) {
        return state.resolveParameterRef(parameterRef.getLibraryName(), parameterRef.getName(), visitor);
    }
}
