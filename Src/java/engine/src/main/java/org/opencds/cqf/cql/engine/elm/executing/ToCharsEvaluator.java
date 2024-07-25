package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

import java.util.ArrayList;
import java.util.List;

/*
ToChars(argument String) List<String>

The ToChars operator takes a string and returns a list with one string for each character in the input, in the order in which they appear in the string.

If the argument is null, the result is null.
*/

public class ToCharsEvaluator {

    public static List<String> toChars(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            List<String> result = new ArrayList<>();
            for (char c : ((String) operand).toCharArray()) {
                result.add(String.valueOf(c));
            }
            return result;
        }

        throw new InvalidOperatorArgument(
                "ToChars(String)",
                String.format("ToInteger(%s)", operand.getClass().getName()));
    }
}
