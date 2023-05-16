package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

import java.util.ArrayList;

public class FunctionRefEvaluator {

    private FunctionDef cachedFunctionDef;
    public Object internalEvaluate(FunctionRef functionRef, State state, CqlEngineVisitor visitor) {
        System.out.println("evaluating Function Ref:"+ functionRef.getName());
        ArrayList<Object> arguments = new ArrayList<>(functionRef.getOperand().size());
        for (Expression operand : functionRef.getOperand()) {
            arguments.add(visitor.visitExpression(operand, state));
        }

        boolean enteredLibrary = state.enterLibrary(functionRef.getLibraryName());
        try {
            FunctionDef functionDef = resolveOrCacheFunctionDef(state, functionRef, arguments);

            if (Boolean.TRUE.equals(functionDef.isExternal())) {
                return state.getExternalFunctionProvider().evaluate(cachedFunctionDef.getName(), arguments);
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

    protected FunctionDef resolveOrCacheFunctionDef(State state, FunctionRef functionRef, ArrayList<Object> arguments) {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        if (this.cachedFunctionDef == null && (arguments.isEmpty() || !functionRef.getSignature().isEmpty())) {
            this.cachedFunctionDef = resolveFunctionDef(state, functionRef, arguments);
        }

        return this.cachedFunctionDef != null ?
                this.cachedFunctionDef :
                resolveFunctionDef(state, functionRef, arguments);
    }

    protected FunctionDef resolveFunctionDef(State context, FunctionRef functionRef, ArrayList<Object> arguments) {
        return context.resolveFunctionRef(functionRef.getLibraryName(), functionRef.getName(), arguments, functionRef.getSignature());
    }
}
