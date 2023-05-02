package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
Upper(argument String) String

The Upper operator returns the upper case of its argument.
If the argument is null, the result is null.
*/

public class UpperEvaluator {

    public static Object upper(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            return ((String) operand).toUpperCase();
        }

        throw new InvalidOperatorArgument(
                "Upper(String)",
                String.format("Upper(%s)", operand.getClass().getName())
        );
    }
}
