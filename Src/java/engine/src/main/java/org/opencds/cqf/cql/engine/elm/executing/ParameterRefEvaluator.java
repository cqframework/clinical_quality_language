package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.ParameterRef;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;

public class ParameterRefEvaluator {
    public static Object internalEvaluate(ParameterRef parameterRef, State state, CqlEngine visitor) {
        boolean enteredLibrary = state.enterLibrary(parameterRef.getLibraryName());
        try {
            var name = parameterRef.getName();
            String fullName = parameterRef.getLibraryName() != null ? String.format("%s.%s", state.getCurrentLibrary().getIdentifier().getId(), name) : name;

            // TODO: Parameter cache
            // if (parameters.containsKey(fullName)) {
            //     return parameters.get(fullName);
            // }

            ParameterDef parameterDef = Libraries.resolveParameterRef(name, state.getCurrentLibrary());
            Object result = parameterDef.getDefault() != null ? visitor.visitExpression(parameterDef.getDefault(), state) : null;

            // parameters.put(fullName, result);
            return result;
        }
        finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
