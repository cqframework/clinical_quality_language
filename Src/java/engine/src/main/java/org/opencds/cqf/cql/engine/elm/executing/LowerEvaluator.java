package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
Lower(argument String) String

The Lower operator returns the lower case of its argument.
If the argument is null, the result is null.
*/

public class LowerEvaluator {

    public static Object lower(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            return ((String) operand).toLowerCase();
        }

        throw new InvalidOperatorArgument(
                "Lower(String)",
                String.format("Lower(%s)", operand.getClass().getName())
        );
    }
}
