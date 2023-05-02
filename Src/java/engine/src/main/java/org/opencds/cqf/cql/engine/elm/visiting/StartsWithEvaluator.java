package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
* StartsWith(argument String, prefix String) Boolean
*
* The StartsWith operator returns true if the given string starts with the given prefix.
*
* If the prefix is the empty string, the result is true.
*
* If either argument is null, the result is null.
*/

public class StartsWithEvaluator {

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

}
