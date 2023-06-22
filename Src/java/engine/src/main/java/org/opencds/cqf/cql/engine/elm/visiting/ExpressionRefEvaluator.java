package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.ExpressionRef;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;

public class ExpressionRefEvaluator{
    public  static Object internalEvaluate(ExpressionRef expressionRef, State state, CqlEngine visitor) {
        boolean enteredLibrary = state.enterLibrary(expressionRef.getLibraryName());
        try {
            var def = state.resolveExpressionRef(expressionRef.getName());
            return visitor.visitExpressionDef(def, state);
        }
        finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
