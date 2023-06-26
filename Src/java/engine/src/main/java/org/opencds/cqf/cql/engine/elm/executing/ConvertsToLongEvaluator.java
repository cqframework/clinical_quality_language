package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*

    ConvertsToLong(argument String) Boolean

    The ConvertsToLong operator returns true if its argument is or can be converted to an Long value. See the ToLong
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is false.

*/

public class ConvertsToLongEvaluator {
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

}
