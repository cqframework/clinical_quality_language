package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.ExpressionRef;
import org.opencds.cqf.cql.engine.execution.Libraries;
import org.opencds.cqf.cql.engine.execution.State;

public class ExpressionRefEvaluator {
    public static Object internalEvaluate(
            ExpressionRef expressionRef, State state, ElmLibraryVisitor<Object, State> visitor) {
        boolean enteredLibrary = state.enterLibrary(expressionRef.getLibraryName());
        try {
            var def = Libraries.resolveExpressionRef(expressionRef.getName(), state.getCurrentLibrary());
            state.pushActivationFrame(def);
            try {
                return visitor.visitExpressionDef(def, state);
            } finally {
                state.popActivationFrame();
            }
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
