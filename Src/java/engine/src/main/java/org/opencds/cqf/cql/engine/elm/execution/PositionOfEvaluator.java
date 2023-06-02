package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
PositionOf(pattern String, argument String) Integer

The PositionOf operator returns the 0-based index of the given pattern in the given string.
If the pattern is not found, the result is -1.
If either argument is null, the result is null.
*/

public class PositionOfEvaluator extends org.cqframework.cql.elm.execution.PositionOf {

    public static Object positionOf(Object pattern, Object string) {
        if (pattern == null || string == null) {
            return null;
        }

        if (pattern instanceof String) {
            return ((String)string).indexOf((String)pattern);
        }

        throw new InvalidOperatorArgument(
                "PositionOf(String, String)",
                String.format("PositionOf(%s, %s)", pattern.getClass().getName(), string.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object pattern = getPattern().evaluate(context);
        Object string = getString().evaluate(context);

        return positionOf(pattern, string);
    }
}
