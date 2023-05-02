package org.opencds.cqf.cql.engine.elm.visiting;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Time;

import java.time.format.DateTimeParseException;

/*

    ConvertsToTime(argument String) Time

    The ConvertsToTime operator returns true if its argument is or can be converted to a Time value. See the ToTime operator for
        a description of the supported conversions.

    If the input string is not formatted correctly, or does not represent a valid time-of-day value, the result is false.

    If the argument is null, the result is null.

*/

public class ConvertsToTimeEvaluator {

    public static Boolean convertsToTime(Object argument) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Time) {
            return true;
        }

        if (argument instanceof String) {
            try {
                new Time((String) argument);
            } catch (DateTimeParseException dtpe) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToTime(String)",
                String.format("ConvertsToTime(%s)", argument.getClass().getName())
        );
    }
}
