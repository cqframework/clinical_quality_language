package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

    _precision_ from(argument Date) Integer
    _precision_ from(argument DateTime) Integer
    _precision_ from(argument Time) Integer
    timezoneoffset from(argument DateTime) Decimal
    date from(argument DateTime) Date
    time from(argument DateTime) Time

    The component-from operator returns the specified component of the argument.

    For Date values, precision must be one of: year, month, or day.

    For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.

    For Time values, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, extraction of a week component is not supported.

    When extracting the Time from a DateTime value, implementations should normalize to the timezone offset of the evaluation request timestamp.

    If the argument is null, or is not specified to the level of precision being extracted, the result is null.

    The following examples illustrate the behavior of the component-from operator:

    define MonthFrom: month from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // 1
    define TimeZoneOffsetFrom: timezoneoffset from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // -7.0
    define DateFrom: date from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // @2012-01-01
    define TimeFrom: time from DateTime(2012, 1, 1, 12, 30, 0, 0, -7) // @T12:30:00.000-07:00
    define MonthFromIsNull: month from DateTime(2012)

*/

public class DateTimeComponentFromEvaluator extends org.cqframework.cql.elm.execution.DateTimeComponentFrom {

    public static Object dateTimeComponentFrom(Object operand, String precision) {

        if (operand == null) {
            return null;
        }

        if (precision == null) {
            throw new InvalidOperatorArgument("Precision must be specified for the _precision_ from operation.");
        }

        Precision p = Precision.fromString(precision);

        if (operand instanceof Date) {
            Date date = (Date)operand;

            if (p.toDateIndex() > date.getPrecision().toDateIndex()) {
                return null;
            }

            return date.getDate().get(p.toChronoField());
        }

        else if (operand instanceof DateTime) {
            DateTime dateTime = (DateTime)operand;

            if (p.toDateTimeIndex() > dateTime.getPrecision().toDateTimeIndex()) {
                return null;
            }

            return dateTime.getDateTime().get(p.toChronoField());
        }

        else if (operand instanceof Time) {
            Time time = (Time)operand;

            if (p.toTimeIndex() > time.getPrecision().toTimeIndex()) {
                return null;
            }

            return time.getTime().get(p.toChronoField());
        }

        throw new InvalidOperatorArgument(
                "_precision_ from(Date), _precision_ from(DateTime) or _precision_ from(Time)",
                String.format("%s from(%s)", precision.toLowerCase(), operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        String precision = getPrecision().value();

        return dateTimeComponentFrom(operand, precision);
    }
}
