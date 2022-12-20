package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Time;

/*
time from(argument DateTime) Time

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/

public class TimeFromEvaluator extends org.cqframework.cql.elm.execution.TimeFrom {

    public static Object timeFrom(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof DateTime) {
            int hour;
            if (((DateTime)operand).getPrecision().toDateTimeIndex() > 2) {
                hour = ((DateTime)operand).getDateTime().getHour();
            }
            else {
                return null;
            }

            int minute;
            if (((DateTime)operand).getPrecision().toDateTimeIndex() > 3) {
                minute = ((DateTime)operand).getDateTime().getMinute();
            }
            else {
                return new Time(hour);
            }

            int second;
            if (((DateTime)operand).getPrecision().toDateTimeIndex() > 4) {
                second = ((DateTime)operand).getDateTime().getSecond();
            }
            else {
                return new Time(hour, minute);
            }

            int millisecond;
            if (((DateTime)operand).getPrecision().toDateTimeIndex() > 5) {
                millisecond = ((DateTime)operand).getDateTime().get(Precision.MILLISECOND.toChronoField());
            }
            else {
                return new Time(hour, minute, second);
            }

            return new Time(hour, minute, second, millisecond);
        }

        throw new InvalidOperatorArgument(
                "TimeFrom(DateTime)",
                String.format("TimeFrom(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return timeFrom(operand);
    }
}
