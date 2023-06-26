package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

import java.util.ArrayList;

public class FunctionRefEvaluator {

    public static Object internalEvaluate(FunctionRef functionRef, State state, CqlEngine visitor) {
        ArrayList<Object> arguments = new ArrayList<>(functionRef.getOperand().size());
        for (Expression operand : functionRef.getOperand()) {
            arguments.add(visitor.visitExpression(operand, state));
        }

        boolean enteredLibrary = state.enterLibrary(functionRef.getLibraryName());
        try {
            FunctionDef functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments);

            if (Boolean.TRUE.equals(functionDef.isExternal())) {
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

    protected static FunctionDef resolveOrCacheFunctionDef(State state, FunctionRef functionRef, ArrayList<Object> arguments) {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        boolean eligibleForCaching = false;
        if (!functionRef.getSignature().isEmpty() || arguments.isEmpty()) {
            eligibleForCaching = true;
            if (state.getCache().getFunctionCache().containsKey(functionRef)) {
                return state.getCache().getFunctionCache().get(functionRef);
            }
        }

        FunctionDef functionDef = resolveFunctionDef(state, functionRef, arguments);

        if (eligibleForCaching && functionDef != null) {
            state.getCache().getFunctionCache().put(functionRef, functionDef);
        }
        return functionDef;
    }

    protected static FunctionDef resolveFunctionDef(State state, FunctionRef functionRef, ArrayList<Object> arguments) {
        return state.resolveFunctionRef(functionRef.getLibraryName(), functionRef.getName(), arguments, functionRef.getSignature());
    }
}
