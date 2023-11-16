package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.*;

import java.math.BigDecimal;

public class AddEvaluator {
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

}
