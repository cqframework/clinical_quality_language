package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
* StartsWith(argument String, prefix String) Boolean
*
* The StartsWith operator returns true if the given string starts with the given prefix.
*
* If the prefix is the empty string, the result is true.
*
* If either argument is null, the result is null.
*/

public class StartsWithEvaluator extends org.cqframework.cql.elm.execution.StartsWith {

    public static Object startsWith(Object argument, Object prefix) {
        if (argument == null || prefix == null) {
            return null;
        }

        if (argument instanceof String && prefix instanceof String) {
            return ((String) argument).startsWith((String) prefix);
        }

        throw new InvalidOperatorArgument(
                "StartsWith(String, String)",
                String.format("StartsWith(%s, %s)", argument.getClass().getName(), prefix.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object argument = getOperand().get(0).evaluate(context);
        Object prefix = getOperand().get(1).evaluate(context);

        return startsWith(argument, prefix);
    }
}
