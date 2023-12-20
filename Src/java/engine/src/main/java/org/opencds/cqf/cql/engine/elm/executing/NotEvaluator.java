package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
not (argument Boolean) Boolean

The not operator returns true if the argument is false and false if the argument is true. Otherwise, the result is null.
*/

public class NotEvaluator {

    public static Boolean not(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Boolean) {
            return !(Boolean) operand;
        }

        throw new InvalidOperatorArgument(
                "Not(Boolean)", String.format("Not(%s)", operand.getClass().getName()));
    }
}
