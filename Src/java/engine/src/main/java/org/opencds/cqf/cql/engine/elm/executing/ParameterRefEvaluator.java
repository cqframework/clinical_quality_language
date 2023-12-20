package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.ParameterRef;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterRefEvaluator {
    private static final Logger log = LoggerFactory.getLogger(ParameterRefEvaluator.class);

    public static Object internalEvaluate(
            ParameterRef parameterRef, State state, ElmLibraryVisitor<Object, State> visitor) {
        boolean enteredLibrary = state.enterLibrary(parameterRef.getLibraryName());
        try {

            ParameterDef parameterDef =
                    Libraries.resolveParameterRef(parameterRef.getName(), state.getCurrentLibrary());
            var name = parameterDef.getName();
            var libraryName = state.getCurrentLibrary().getIdentifier().getId();

            String fullName = String.format("%s.%s", libraryName, name);

            if (state.getParameters().containsKey(fullName)) {
                return state.getParameters().get(fullName);
            }

            if (state.getParameters().containsKey(parameterDef.getName())) {
                log.debug(
                        "Using global value for parameter \"{}\" while evaluating in library \"{}\"",
                        parameterDef.getName(),
                        state.getCurrentLibrary().getIdentifier().getId());
                return state.getParameters().get(parameterDef.getName());
            }

            Object result = parameterDef.getDefault() != null
                    ? visitor.visitExpression(parameterDef.getDefault(), state)
                    : null;

            state.getParameters().put(fullName, result);
            return result;
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
