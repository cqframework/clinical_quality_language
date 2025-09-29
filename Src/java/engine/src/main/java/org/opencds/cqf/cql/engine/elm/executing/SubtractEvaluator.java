package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.*;

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

public class SubtractEvaluator {

    public static Object subtract(Object left, Object right, final State state) {
        if (left == null || right == null) {
            return null;
        }

        // -(Integer, Integer)
        if (left instanceof Integer leftInteger && right instanceof Integer rightInteger) {
            return leftInteger - rightInteger;
        } else if (left instanceof Long leftLong && right instanceof Long rightLong) {
            return leftLong - rightLong;
        }
        // -(Decimal, Decimal)
        else if (left instanceof BigDecimal leftBigDecimal && right instanceof BigDecimal rightBigDecimal) {
            return leftBigDecimal.subtract(rightBigDecimal);
        }
        // -(Quantity, Quantity)
        else if (left instanceof Quantity leftQuantity && right instanceof Quantity rightQuantity) {
            return UnitConversionHelper.computeWithConvertedUnits(
                    leftQuantity,
                    rightQuantity,
                    (commonUnit, leftValue, rightValue) ->
                            new Quantity().withUnit(commonUnit).withValue(leftValue.subtract(rightValue)),
                    state);
        }
        // -(DateTime, Quantity)
        else if (left instanceof BaseTemporal && right instanceof Quantity rightQuantity) {
            Precision valueToSubtractPrecision = Precision.fromString(rightQuantity.getUnit());
            Precision precision = Precision.fromString(BaseTemporal.getLowestPrecision((BaseTemporal) left));
            int valueToSubtract = rightQuantity.getValue().intValue();

            if (left instanceof DateTime || left instanceof Date) {
                if (valueToSubtractPrecision == Precision.WEEK) {
                    valueToSubtract = TemporalHelper.weeksToDays(valueToSubtract);
                    valueToSubtractPrecision = Precision.DAY;
                }
            }

            long convertedValueToSubtract = valueToSubtract;
            if (precision.toDateTimeIndex() < valueToSubtractPrecision.toDateTimeIndex()) {
                convertedValueToSubtract = TemporalHelper.truncateValueToTargetPrecision(
                        valueToSubtract, valueToSubtractPrecision, precision);
                valueToSubtractPrecision = precision;
            }

            if (left instanceof DateTime) {
                return new DateTime(
                        ((DateTime) left)
                                .getDateTime()
                                .minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()),
                        precision);
            } else if (left instanceof Date) {
                return new Date(((Date) left)
                                .getDate()
                                .minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()))
                        .setPrecision(precision);
            } else {
                return new Time(
                        ((Time) left)
                                .getTime()
                                .minus(convertedValueToSubtract, valueToSubtractPrecision.toChronoUnit()),
                        precision);
            }
        } else if (left instanceof Interval leftInterval && right instanceof Interval rightInterval) {
            return new Interval(
                    subtract(leftInterval.getStart(), rightInterval.getStart(), state),
                    true,
                    subtract(leftInterval.getEnd(), rightInterval.getEnd(), state),
                    true);
        }

        throw new InvalidOperatorArgument(
                "Subtract(Integer, Integer), Subtract(Long, Long) Subtract(Decimal, Decimal), Subtract(Quantity, Quantity), Subtract(Date, Quantity), Subtract(DateTime, Quantity), Subtract(Time, Quantity)",
                String.format(
                        "Subtract(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
