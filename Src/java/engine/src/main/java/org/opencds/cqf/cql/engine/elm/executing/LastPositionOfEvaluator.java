package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
 * LastPositionOf(pattern String, argument String) Integer
 *
 * The LastPositionOf operator returns the 0-based index of the last appearance of the given pattern in the given string.
 *
 * If the pattern is not found, the result is -1.
 *
 * If either argument is null, the result is null.
 */

public class LastPositionOfEvaluator {

    public static Object lastPositionOf(Object string, Object pattern) {
        if (pattern == null || string == null) {
            return null;
        }

        if (pattern instanceof String) {
            return ((String) string).lastIndexOf((String) pattern);
        }

        throw new InvalidOperatorArgument(
                "LastPositionOf(String, String)",
                String.format(
                        "LastPositionOf(%s, %s)",
                        pattern.getClass().getName(), string.getClass().getName()));
    }
}
