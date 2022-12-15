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
import org.opencds.cqf.cql.engine.runtime.Value;

/*

*** NOTES FOR ARITHMETIC OPERATORS ***
+(left Integer, right Integer) Integer
+(left Long, right Long) Long
+(left Decimal, right Decimal) Decimal
+(left Quantity, right Quantity) Quantity

The add (+) operator performs numeric addition of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
TODO: When adding quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
  For example, units of 'cm' and 'm' can be added, but units of 'cm2' and  'cm' cannot.
    The unit of the result will be the most granular unit of either input.
If either argument is null, the result is null.


*** NOTES FOR DATETIME ***
+(left Date, right Quantity) Date
+(left DateTime, right Quantity) DateTime
+(left Time, right Quantity) Time

The add (+) operator returns the value of the given date/time, incremented by the time-valued quantity, respecting
    variable length periods for calendar years and months.

For Date values, the quantity unit must be one of: years, months, weeks, or days.
For DateTime values, the quantity unit must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.
For Time values, the quantity unit must be one of: hours, minutes, seconds, or milliseconds.

Note that the quantity units may be specified in singular, plural, or UCUM form.

The operation is performed by converting the time-based quantity to the most precise value specified in the date/time
    (truncating any resulting decimal portion) and then adding it to the date/time value.
    For example, the following addition:
        DateTime(2014) + 24 months
    This example results in the value DateTime(2016) even though the date/time value is not specified to the level of precision of the time-valued quantity.
Note also that this means that if decimals appear in the time-valued quantities, the fractional component will be ignored.
    For example, the following addition:
        DateTime(2014) + 18 months
    This example results in the value DateTime(2015)

If either argument is null, the result is null.

*/

public class AddEvaluator extends org.cqframework.cql.elm.execution.Add {

    public static Object add(Object left, Object right) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer && right instanceof Integer) {
            return (Integer)left + (Integer)right;
        }

        if (left instanceof Long && right instanceof Long) {
            return (Long)left + (Long)right;
        }

        else if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return Value.verifyPrecision(((BigDecimal)left).add((BigDecimal)right), null);
        }

        else if (left instanceof Quantity && right instanceof Quantity) {
            return new Quantity().withValue((((Quantity)left).getValue()).add(((Quantity)right).getValue())).withUnit(((Quantity)left).getUnit());
        }

        //+(DateTime, Quantity), +(Date, Quantity), +(Time, Quantity)
        else if (left instanceof BaseTemporal && right instanceof Quantity) {
            Precision valueToAddPrecision = Precision.fromString(((Quantity) right).getUnit());
            Precision precision = Precision.fromString(BaseTemporal.getLowestPrecision((BaseTemporal) left));
            int valueToAdd = ((Quantity) right).getValue().intValue();

            if (left instanceof DateTime || left instanceof Date) {
                if (valueToAddPrecision == Precision.WEEK) {
                    valueToAdd = TemporalHelper.weeksToDays(valueToAdd);
                    valueToAddPrecision = Precision.DAY;
                }
            }

            if (left instanceof DateTime || left instanceof Date) {
                if (precision == Precision.WEEK) {
                    valueToAdd = TemporalHelper.weeksToDays(valueToAdd);
                    precision = Precision.DAY;
                }
            }
            long convertedValueToAdd = valueToAdd;
            if (precision.toDateTimeIndex() < valueToAddPrecision.toDateTimeIndex()) {
                convertedValueToAdd = TemporalHelper.truncateValueToTargetPrecision(valueToAdd, valueToAddPrecision, precision);
                valueToAddPrecision = precision;
            }

            if (left instanceof DateTime) {
                return new DateTime(((DateTime) left).getDateTime().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()), precision);
            } else if (left instanceof Date) {
                return new Date(((Date) left).getDate().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit())).setPrecision(precision);
            } else {
                return new Time(((Time) left).getTime().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()), precision);
            }
        }

        // +(Uncertainty, Uncertainty)
        else if (left instanceof Interval && right instanceof Interval) {
            Interval leftInterval = (Interval)left;
            Interval rightInterval = (Interval)right;
            return new Interval(add(leftInterval.getStart(), rightInterval.getStart()), true, add(leftInterval.getEnd(), rightInterval.getEnd()), true);
        }

        else if (left instanceof String && right instanceof String) {
            return ((String) left).concat((String) right);
        }

        throw new InvalidOperatorArgument(
                "Add(Integer, Integer), Add(Long, Long), Add(Decimal, Decimal), Add(Quantity, Quantity), Add(Date, Quantity), Add(DateTime, Quantity) or Add(Time, Quantity)",
                String.format("Add(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return add(left, right);
    }
}
