package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ExpressionRef;
import org.opencds.cqf.cql.engine.execution.State;

public class ExpressionRefEvaluator{
    public  static Object internalEvaluate(ExpressionRef expressionRef, State state) {
        boolean enteredLibrary = state.enterLibrary(expressionRef.getLibraryName());
        try {
            return state.resolveExpressionRef(expressionRef.getName());
        }
        finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
