package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.BaseTemporal;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Precision;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;
import org.opencds.cqf.cql.engine.runtime.Time;

/*

*** NOTES FOR ARITHMETIC OPERATOR ***
-(left Integer, right Integer) Integer
-(left Decimal, right Decimal) Decimal
-(left Quantity, right Quantity) Quantity

The subtract (-) operator performs numeric subtraction of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
When subtracting quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
  For example, units of 'cm' and 'm' can be subtracted, but units of 'cm2' and  'cm' cannot.
    The unit of the result will be the most granular unit of either input.
If either argument is null, the result is null.

*** NOTES FOR DATETIME ***
-(left Date, right Quantity) Date
-(left DateTime, right Quantity) DateTime
-(left Time, right Quantity) Time

The subtract (-) operator returns the value of the given date/time, decremented by the time-valued quantity, respecting
    variable length periods for calendar years and months.

For Date values, the quantity unit must be one of: years, months, weeks, or days.

For DateTime values, the quantity unit must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.

For Time values, the quantity unit must be one of: hours, minutes, seconds, or milliseconds.

Note that the quantity units may be specified in singular, plural or UCUM form.

The operation is performed by converting the time-based quantity to the most precise value specified in the date/time
    (truncating any resulting decimal portion) and then subtracting it from the date/time value.
    For example, the following subtraction:
        DateTime(2014) - 24 months
    This example results in the value DateTime(2012) even though the date/time value is not specified to the level of precision of the time-valued quantity.

Note also that this means that if decimals appear in the time-valued quantities, the fractional component will be ignored.
    For example, the following subtraction:
        DateTime(2014) - 18 months
    This example results in the value DateTime(2013)

If either argument is null, the result is null.

NOTE: see note in AddEvaluator

*/

public class SubtractEvaluator extends org.cqframework.cql.elm.execution.Subtract {

    public static Object subtract(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        // -(Integer, Integer)
        if (left instanceof Integer) {
            return (Integer)left - (Integer)right;
        }

        if (left instanceof Long) {
            return (Long)left - (Long)right;
        }

        // -(Decimal, Decimal)
        else if (left instanceof BigDecimal) {
            return ((BigDecimal)left).subtract((BigDecimal)right);
        }

        // -(Quantity, Quantity)
        else if (left instanceof Quantity) {
            return new Quantity().withValue((((Quantity)left).getValue()).subtract(((Quantity)right).getValue())).withUnit(((Quantity)left).getUnit());
        }

        // -(DateTime, Quantity)
        else if (left instanceof BaseTemporal && right instanceof Quantity) {
            Precision valueToSubtractPrecision = Precision.fromString(((Quantity) right).getUnit());
            Precision precision = Precision.fromString(BaseTemporal.getLowestPrecision((BaseTemporal) left));
            int valueToSubtract = ((Quantity) right).getValue().intValue();

            if (left instanceof DateTime || left instanceof Date) {
                if (valueToSubtractPrecision == Precision.WEEK) {
                    valueToSubtract = TemporalHelper.weeksToDays(valueToSubtract);
                    valueToSubtractPrecision = Precision.DAY;
                }
            }

            long convertedValueToSubtract = valueToSubtract;
            if (precision.toDateTimeIndex() < valueToSubtractPrecision.toDateTimeIndex()) {
                convertedValueToSubtract = TemporalHelper.truncateValueToTargetPrecision(valueToSubtract, valueToSubtractPrecision, precision);
                valueToSubtractPrecision = precision;
            }

            if (left instanceof DateTime) {
                return new DateTime(((DateTime) left).getDateTime().minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()), precision);
            } else if (left instanceof Date) {
                return new Date(((Date) left).getDate().minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit())).setPrecision(precision);
            } else {
                return new Time(((Time) left).getTime().minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()), precision);
            }
        }

        else if (left instanceof Interval && right instanceof Interval) {
            Interval leftInterval = (Interval)left;
            Interval rightInterval = (Interval)right;
            return new Interval(subtract(leftInterval.getStart(), rightInterval.getStart()), true, subtract(leftInterval.getEnd(), rightInterval.getEnd()), true);
        }

        throw new InvalidOperatorArgument(
            "Subtract(Integer, Integer), Subtract(Long, Long) Subtract(Decimal, Decimal), Subtract(Quantity, Quantity), Subtract(Date, Quantity), Subtract(DateTime, Quantity), Subtract(Time, Quantity)",
            String.format("Subtract(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return subtract(left, right);
    }
}
