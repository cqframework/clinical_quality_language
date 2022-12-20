package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

difference in _precision_ between(low Date, high Date) Integer
difference in _precision_ between(low DateTime, high DateTime) Integer
difference in _precision_ between(low Time, high Time) Integer

The difference-between operator returns the number of boundaries crossed for the specified precision between the first
    and second arguments. If the first argument is after the second argument, the result is negative. The result of this
    operation is always an integer; any fractional boundaries are dropped.

As with all date/time calculations, difference calculations are performed respecting the timezone offset depending on the precision.

For Date values, precision must be one of: years, months, weeks, or days.
For DateTime values, precision must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.
For Time values, precision must be one of: hours, minutes, seconds, or milliseconds.

For calculations involving weeks, Sunday is considered to be the first day of the week for the purposes of determining the number of boundaries crossed.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

If either argument is null, the result is null.

Additional Complexity: precision elements above the specified precision must also be accounted for.
For example:
days between DateTime(2011, 5, 1) and DateTime(2012, 5, 6) = 365 + 5 = 370 days

NOTE: This is the same operation as DurationBetween, but the precision after the specified precision is truncated
to get the number of boundaries crossed instead of whole calendar periods.
For Example:
difference in days between DateTime(2014, 5, 12, 12, 10) and DateTime(2014, 5, 25, 15, 55)
will truncate the DateTimes to:
DateTime(2014, 5, 12) and DateTime(2014, 5, 25) respectively

*/

public class DifferenceBetweenEvaluator extends org.cqframework.cql.elm.execution.DifferenceBetween {

    public static Object difference(Object left, Object right, Precision precision) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            boolean isWeeks = false;
            if (precision == Precision.WEEK) {
                isWeeks = true;
                precision = Precision.DAY;
            }
            boolean isLeftUncertain = ((BaseTemporal) left).isUncertain(precision);
            boolean isRightUncertain = ((BaseTemporal) right).isUncertain(precision);
            if (isLeftUncertain && isRightUncertain) {
                return null;
            }
            if (isLeftUncertain) {
                Interval leftUncertainInterval = ((BaseTemporal) left).getUncertaintyInterval(precision);
                return new Interval(
                        difference(leftUncertainInterval.getEnd(), right, isWeeks ? Precision.WEEK : precision), true,
                        difference(leftUncertainInterval.getStart(), right, isWeeks ? Precision.WEEK : precision), true
                ).setUncertain(true);
            }
            if (isRightUncertain) {
                Interval rightUncertainInterval = ((BaseTemporal) right).getUncertaintyInterval(precision);
                return new Interval(
                        difference(left, rightUncertainInterval.getStart(), isWeeks ? Precision.WEEK : precision), true,
                        difference(left, rightUncertainInterval.getEnd(), isWeeks ? Precision.WEEK : precision), true
                ).setUncertain(true);
            }

            if (left instanceof DateTime && right instanceof DateTime) {
                if (precision.toDateTimeIndex() <= Precision.DAY.toDateTimeIndex()) {
                    return isWeeks
                            ? (int) precision.toChronoUnit().between(
                                ((DateTime) left).expandPartialMinFromPrecision(precision).getDateTime().toLocalDate(),
                                ((DateTime) right).expandPartialMinFromPrecision(precision).getDateTime().toLocalDate()) / 7
                            : (int) precision.toChronoUnit().between(
                                ((DateTime) left).expandPartialMinFromPrecision(precision).getDateTime().toLocalDate(),
                                ((DateTime) right).expandPartialMinFromPrecision(precision).getDateTime().toLocalDate());
                }
                else {
                    return (int) precision.toChronoUnit().between(
                            ((DateTime) left).expandPartialMinFromPrecision(precision).getDateTime(),
                            ((DateTime) right).expandPartialMinFromPrecision(precision).getDateTime());
                }
            }

            if (left instanceof Date && right instanceof Date) {
                return isWeeks
                        ? (int) precision.toChronoUnit().between(
                                ((Date) left).expandPartialMinFromPrecision(precision).getDate(),
                                ((Date) right).expandPartialMinFromPrecision(precision).getDate()) / 7
                        : (int) precision.toChronoUnit().between(
                                ((Date) left).expandPartialMinFromPrecision(precision).getDate(),
                                ((Date) right).expandPartialMinFromPrecision(precision).getDate());
            }

            if (left instanceof Time && right instanceof Time) {
                return (int) precision.toChronoUnit().between(
                        ((Time) left).expandPartialMinFromPrecision(precision).getTime(),
                        ((Time) right).expandPartialMinFromPrecision(precision).getTime()
                );
            }
        }

        throw new InvalidOperatorArgument(
                "DifferenceBetween(Date, Date), DifferenceBetween(DateTime, DateTime), DifferenceBetween(Time, Time)",
                String.format("DifferenceBetween(%s, %s)", left.getClass().getName(), right.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision().value();

        return difference(left, right, Precision.fromString(precision));
    }
}
