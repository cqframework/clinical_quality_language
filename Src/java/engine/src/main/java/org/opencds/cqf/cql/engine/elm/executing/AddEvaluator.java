package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.*;

public class AddEvaluator {
    public static Object add(final Object left, final Object right, final State state) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer leftInteger && right instanceof Integer rightInteger) {
            return leftInteger + rightInteger;
        } else if (left instanceof Long leftLong && right instanceof Long rightLong) {
            return leftLong + rightLong;
        } else if (left instanceof BigDecimal leftBigDecimal && right instanceof BigDecimal rightBigDecimal) {
            return Value.verifyPrecision(leftBigDecimal.add(rightBigDecimal), null);
        } else if (left instanceof Quantity leftQuantity && right instanceof Quantity rightQuantity) {
            return UnitConversionHelper.computeWithConvertedUnits(
                    leftQuantity,
                    rightQuantity,
                    (commonUnit, leftValue, rightValue) ->
                            new Quantity().withUnit(commonUnit).withValue(leftValue.add(rightValue)),
                    state);
        }

        // +(DateTime, Quantity), +(Date, Quantity), +(Time, Quantity)
        else if (left instanceof BaseTemporal && right instanceof Quantity rightQuantity) {
            Precision valueToAddPrecision = Precision.fromString(rightQuantity.getUnit());
            Precision precision = Precision.fromString(BaseTemporal.getLowestPrecision((BaseTemporal) left));
            int valueToAdd = rightQuantity.getValue().intValue();

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
                convertedValueToAdd =
                        TemporalHelper.truncateValueToTargetPrecision(valueToAdd, valueToAddPrecision, precision);
                valueToAddPrecision = precision;
            }

            if (left instanceof DateTime leftDateTime) {
                return new DateTime(
                        leftDateTime.getDateTime().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()),
                        precision);
            } else if (left instanceof Date leftDate) {
                return new Date(leftDate.getDate().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()))
                        .setPrecision(precision);
            } else {
                return new Time(
                        ((Time) left).getTime().plus(convertedValueToAdd, valueToAddPrecision.toChronoUnit()),
                        precision);
            }
        }

        // +(Uncertainty, Uncertainty)
        else if (left instanceof Interval leftInterval && right instanceof Interval rightInterval) {
            return new Interval(
                    add(leftInterval.getStart(), rightInterval.getStart(), state),
                    true,
                    add(leftInterval.getEnd(), rightInterval.getEnd(), state),
                    true);
        } else if (left instanceof String && right instanceof String) {
            return ((String) left).concat((String) right);
        }

        throw new InvalidOperatorArgument(
                "Add(Integer, Integer), Add(Long, Long), Add(Decimal, Decimal), Add(Quantity, Quantity), Add(Date, Quantity), Add(DateTime, Quantity) or Add(Time, Quantity)",
                String.format(
                        "Add(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
