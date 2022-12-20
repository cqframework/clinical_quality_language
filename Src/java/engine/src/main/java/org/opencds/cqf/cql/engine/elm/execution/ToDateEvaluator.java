package org.opencds.cqf.cql.engine.elm.execution;

import java.time.format.DateTimeParseException;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;

/*

ToDate(argument String) DateTime

The ToDate operator converts the value of its argument to a Date value.
The operator expects the string to be formatted using the ISO-8601 date representation:
    YYYY-MM-DD

In addition, the string must be interpretable as a valid date value.
For example, the following are valid string representations for date values:
    '2014-01' // January, 2014
    '2014-01-01' // January 1st, 2014

If the input string is not formatted correctly, or does not represent a valid date value, the result is null.
As with date literals, date values may be specified to any precision.
If the argument is null, the result is null.

*/

public class ToDateEvaluator extends org.cqframework.cql.elm.execution.ToDate {

    public static Object toDate(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Date) {
            return operand;
        }

        if (operand instanceof String) {
            try {
                return new Date((String) operand);
            } catch (DateTimeParseException dtpe) {
                return null;
            }
        }

        if (operand instanceof DateTime) {
            return new Date(((DateTime) operand).getDateTime().toLocalDate())
                    .setPrecision(
                            ((DateTime) operand).getPrecision().toDateTimeIndex() > 2 ? Precision.DAY : ((DateTime) operand).getPrecision()
                    );
        }

        throw new InvalidOperatorArgument(
                "ToDate(String)",
                String.format("ToDate(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return toDate(operand);
    }
}
