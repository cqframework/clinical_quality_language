package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;

import org.cqframework.cql.elm.execution.Expression;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.Variable;

public class FunctionRefEvaluator extends org.cqframework.cql.elm.execution.FunctionRef {

    private FunctionDef cachedFunctionDef;

    @Override
    protected Object internalEvaluate(Context context) {
        ArrayList<Object> arguments = new ArrayList<>(this.getOperand().size());
        for (Expression operand : this.getOperand()) {
            arguments.add(operand.evaluate(context));
        }

        boolean enteredLibrary = context.enterLibrary(this.getLibraryName());
        try {

            FunctionDef functionDef = this.resolveOrCacheFunctionDef(context, arguments);

            if (Boolean.TRUE.equals(functionDef.isExternal())) {
                return context.getExternalFunctionProvider().evaluate(cachedFunctionDef.getName(), arguments);
            }
            else {
                context.pushWindow();
                try {
                    for (int i = 0; i < arguments.size(); i++) {
                        context.push(new Variable().withName(functionDef.getOperand().get(i).getName()).withValue(arguments.get(i)));
                    }
                    return functionDef.getExpression().evaluate(context);
                }
                finally {
                    context.popWindow();
                }
            }
        }
        finally {
            context.exitLibrary(enteredLibrary);
        }
    }

    protected FunctionDef resolveOrCacheFunctionDef(Context context, ArrayList<Object> arguments) {
        // We can cache a function ref if:
        // 1. ELM signatures are provided OR
        // 2. No arguments are provided (only one overload anyway)
        if (cachedFunctionDef == null && (arguments.isEmpty() || !this.getSignature().isEmpty())) {
            this.cachedFunctionDef = this.resolveFunctionDef(context, arguments);
        }

        return this.cachedFunctionDef != null ?
        cachedFunctionDef :
        this.resolveFunctionDef(context, arguments);
    }

    protected FunctionDef resolveFunctionDef(Context context, ArrayList<Object> arguments) {
        return context.resolveFunctionRef(this.getLibraryName(), this.getName(), arguments, this.getSignature());
    }
}
