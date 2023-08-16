package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.OperandRef;
import org.opencds.cqf.cql.engine.execution.State;

public class OperandRefEvaluator {

    public static Object internalEvaluate(OperandRef operandRef, State state, ElmLibraryVisitor<Object, State> visitor) {
        var variable = state.resolveVariable(operandRef.getName(), true).getValue();
        // We're executing the logic here, so this is valid check in execution context
        if (variable instanceof ExpressionDef) {
            return visitor.visitExpressionDef((ExpressionDef)variable, state);
        }
        else {
            return variable;
        }
    }
}
