package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.Optional;

import org.cqframework.cql.elm.execution.Expression;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.Variable;

public class FunctionRefEvaluator extends org.cqframework.cql.elm.execution.FunctionRef {

    @Override
    protected Object internalEvaluate(Context context) {
        ArrayList<Object> arguments = new ArrayList<>();
        for (Expression operand : this.getOperand()) {
            arguments.add(operand.evaluate(context));
        }

        boolean enteredLibrary = context.enterLibrary(this.getLibraryName());
        try {
            // TODO: Use type specifiers from the operands here if they are available
            FunctionDef functionDef = context.resolveFunctionRef(this.getName(), arguments, this.getLibraryName());
            if (Optional.ofNullable(functionDef.isExternal()).orElse(false)) {
                return context.getExternalFunctionProvider().evaluate(functionDef.getName(), arguments);
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
}
