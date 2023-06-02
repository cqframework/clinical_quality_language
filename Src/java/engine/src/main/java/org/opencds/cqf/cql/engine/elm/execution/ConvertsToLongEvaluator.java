package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*

    ConvertsToLong(argument String) Boolean

    The ConvertsToLong operator returns true if its argument is or can be converted to an Long value. See the ToLong
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/

public class ConvertsToLongEvaluator extends org.cqframework.cql.elm.execution.ConvertsToLong {
    public static Boolean convertsToLong(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Boolean) {
            return true;
        }

        if (argument instanceof Integer) {
            return true;
        }

        if (argument instanceof String) {
            try {
                Long.valueOf((String) argument);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToLong(String)",
                String.format("ConvertsToLong(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return convertsToLong(operand);
    }
}
