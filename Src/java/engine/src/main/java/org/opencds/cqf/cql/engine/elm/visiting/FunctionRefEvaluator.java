package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

import java.util.ArrayList;
import java.util.Optional;

public class FunctionRefEvaluator {

    public static Object internalEvaluate(FunctionRef functionRef, State state, CqlEngineVisitor visitor) {
        ArrayList<Object> arguments = new ArrayList<>(functionRef.getOperand().size());
        for (Expression operand : functionRef.getOperand()) {
            arguments.add(visitor.visitExpression(operand, state));
        }

        boolean enteredLibrary = state.enterLibrary(functionRef.getLibraryName());
        try {
            FunctionDef functionDef = state.resolveFunctionRef(functionRef.getLibraryName(), functionRef.getName(), arguments, functionRef.getSignature());

            if (Optional.ofNullable(functionDef.isExternal()).orElse(false)) {
                return state.getExternalFunctionProvider().evaluate(functionDef.getName(), arguments);
            } else {
                state.pushWindow();
                try {
                    for (int i = 0; i < arguments.size(); i++) {
                        state.push(new Variable().withName(functionDef.getOperand().get(i).getName()).withValue(arguments.get(i)));
                    }
                    return visitor.visitExpression(functionDef.getExpression(), state);
                } finally {
                    state.popWindow();
                }
            }
        } finally {
            state.exitLibrary(enteredLibrary);
        }
    }
}
