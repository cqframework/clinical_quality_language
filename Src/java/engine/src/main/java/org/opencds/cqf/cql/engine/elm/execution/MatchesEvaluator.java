package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

public class MatchesEvaluator extends org.cqframework.cql.elm.execution.Matches {

    public static Object matches(String argument, String pattern) {
        if (argument == null || pattern == null) {
            return null;
        }

        return argument.matches(pattern);
    }

    @Override
    protected Object internalEvaluate(Context context) {
        String argument = (String) getOperand().get(0).evaluate(context);
        String pattern = (String) getOperand().get(1).evaluate(context);

        return matches(argument, pattern);
    }
}
