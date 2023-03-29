package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cqframework.cql.elm.execution.Expression;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.Variable;

public class FunctionRefEvaluator extends org.cqframework.cql.elm.execution.FunctionRef {

    @Override
    protected Object internalEvaluate(Context context) {
        ArrayList<Object> arguments = new ArrayList<>(this.getOperand().size());
        for (Expression operand : this.getOperand()) {
            arguments.add(operand.evaluate(context));
        }

        boolean enteredLibrary = context.enterLibrary(this.getLibraryName());
        try {
            FunctionDef functionDef = context.resolveFunctionRef(this.getLibraryName(), this.getName(), arguments, this.getSignature());

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
