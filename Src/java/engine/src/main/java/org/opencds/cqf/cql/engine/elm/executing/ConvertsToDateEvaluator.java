package org.opencds.cqf.cql.engine.elm.executing;

import java.time.format.DateTimeParseException;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Date;

/*

    ConvertsToDate(argument String) Boolean

    The ConvertsToDate operator returns true if its argument is or can be converted to a Date value. See the ToDate operator
        for a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid date value, the result is false.

    As with date literals, date values may be specified to any precision.

    If the argument is null, the result is null.

*/

public class ConvertsToDateEvaluator {

    public static Boolean convertsToDate(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Date) {
            return true;
        }

        if (argument instanceof String) {
            try {
                new Date((String) argument);
            } catch (DateTimeParseException dtpe) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToDate(String)",
                String.format("ConvertsToDate(%s)", argument.getClass().getName()));
    }
}
