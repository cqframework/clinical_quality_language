package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;

/*
simple type DateTime

The DateTime type represents date and time values with potential uncertainty within CQL.
CQL supports date and time values in the range @0001-01-01T00:00:00.0 to @9999-12-31T23:59:59.999 with a 1 millisecond step size.
*/

public class DateTimeEvaluator {

    public static Object internalEvaluate(Integer year, Integer month, Integer day,
                                          Integer hour, Integer minute, Integer second,
                                          Integer milliSecond, BigDecimal timeZoneOffset) {

        if (year == null) {
            return null;
        }

        try {
            return new DateTime(
                    timeZoneOffset,
                    TemporalHelper.cleanArray(
                            year,
                            month,
                            day,
                            hour,
                            minute,
                            second,
                            milliSecond
                    )
            );
        }
        catch (DateTimeParseException e) {
            throw new InvalidDateTime(
                    String.format("Invalid date time components %s", e.getMessage()),
                    e
            );
        }
    }
}
