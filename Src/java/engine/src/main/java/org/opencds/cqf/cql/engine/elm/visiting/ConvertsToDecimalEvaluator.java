package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

import java.math.BigDecimal;

/*

    ConvertsToDecimal(argument String) Boolean

    The ToDecimal operator returns true if its argument is or can be converted to a Decimal value. See the ToDecimal operator
        for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Decimal value, the result is false.

    If the argument is null, the result is null.

*/

public class ConvertsToDecimalEvaluator {

    public static Boolean convertsToDecimal(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Boolean) {
            return true;
        }

        if (argument instanceof Integer) {
            return true;
        }

        if (argument instanceof BigDecimal) {
            return true;
        }

        if (argument instanceof String) {
            try {
                Double.valueOf((String) argument);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToDecimal(String)",
                String.format("ConvertsToDecimal(%s)", argument.getClass().getName())
        );
    }
}
