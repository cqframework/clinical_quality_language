package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*

    ConvertsToInteger(argument String) Boolean

    The ConvertsToInteger operator returns true if its argument is or can be converted to an Integer value. See the ToInteger
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/

public class ConvertsToIntegerEvaluator extends org.cqframework.cql.elm.execution.ConvertsToInteger {
    public static Boolean convertsToInteger(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Boolean) {
            return true;
        }

        if (argument instanceof Integer) {
            return true;
        }

        if (argument instanceof Long) {
            return true;
        }

        if (argument instanceof String) {
            try {
                Integer.valueOf((String) argument);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToInteger(String)",
                String.format("ConvertsToInteger(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return convertsToInteger(operand);
    }
}
