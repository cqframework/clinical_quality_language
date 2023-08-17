package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.ParameterRef;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;

public class ParameterRefEvaluator {
    public static Object internalEvaluate(ParameterRef parameterRef, State state, ElmLibraryVisitor<Object, State> visitor) {
        boolean enteredLibrary = state.enterLibrary(parameterRef.getLibraryName());
        try {
            var name = parameterRef.getName();
            String fullName = parameterRef.getLibraryName() != null ? String.format("%s.%s", state.getCurrentLibrary().getIdentifier().getId(), name) : name;

            if (state.getParameters().containsKey(fullName)) {
                return state.getParameters().get(fullName);
            }

            ParameterDef parameterDef = Libraries.resolveParameterRef(name, state.getCurrentLibrary());
            Object result = parameterDef.getDefault() != null ? visitor.visitExpression(parameterDef.getDefault(), state) : null;

            state.getParameters().put(fullName, result);
            return result;
        }
        finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
