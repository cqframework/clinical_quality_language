package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class EndsWithEvaluator extends org.cqframework.cql.elm.execution.EndsWith {

    public static Object endsWith(String argument, String suffix) {
        if (argument == null || suffix == null) {
            return null;
        }
        return argument.endsWith(suffix);
    }

    @Override
    protected Object internalEvaluate(Context context) {
        String argument = (String) getOperand().get(0).evaluate(context);
        String suffix = (String) getOperand().get(1).evaluate(context);

        return endsWith(argument, suffix);
    }
}
