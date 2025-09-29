package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import org.fhir.ucum.Decimal;
import org.fhir.ucum.Pair;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
*(left Integer, right Integer) Integer
*(left Long, right Long) Long
*(left Decimal, right Decimal) Decimal
*(left Decimal, right Quantity) Quantity
*(left Quantity, right Decimal) Quantity
*(left Quantity, right Quantity) Quantity

The multiply (*) operator performs numeric multiplication of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
For multiplication operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm' * 3 'cm'
3 'cm' * 12 'cm2'
In this example, the first result will have a unit of 'cm2', and the second result will have a unit of 'cm3'.
If either argument is null, the result is null.
*/

public class MultiplyEvaluator {

    public static Object multiply(Object left, Object right, final State state) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer leftInteger) {
            return leftInteger * (Integer) right;
        } else if (left instanceof Long leftLong) {
            return leftLong * (Long) right;
        } else if (left instanceof BigDecimal && right instanceof BigDecimal) { // *(Decimal, Decimal)
            return Value.verifyPrecision(((BigDecimal) left).multiply((BigDecimal) right), null);
        } else if (left instanceof Quantity leftQuantity && right instanceof Quantity rightQuantity) {
            final var leftValue = leftQuantity.getValue();
            final var leftUnit = leftQuantity.getUnit();
            final var rightValue = rightQuantity.getValue();
            final var rightUnit = rightQuantity.getUnit();
            final BigDecimal unverifiedResultValue;
            final String resultUnit;
            // Two fast-path cases: if either unit is "1", skip unit conversion
            if (leftUnit.equals("1")) {
                unverifiedResultValue = leftValue.multiply(rightValue);
                resultUnit = rightUnit;
            } else if (rightUnit.equals("1")) {
                unverifiedResultValue = leftValue.multiply(rightValue);
                resultUnit = leftUnit;
            } else {
                final var ucumService =
                        state.getEnvironment().getLibraryManager().getUcumService();
                final Pair result;
                try {
                    result = ucumService.multiply(
                            new Pair(new Decimal(String.valueOf(leftValue)), leftUnit),
                            new Pair(new Decimal(String.valueOf(rightValue)), rightUnit));
                } catch (UcumException e) {
                    throw new RuntimeException(e);
                }
                unverifiedResultValue = new BigDecimal(result.getValue().asDecimal());
                final var rawResultUnit = result.getCode();
                resultUnit = rawResultUnit.isEmpty() ? "1" : rawResultUnit;
            }
            final var resultValue = Value.verifyPrecision(unverifiedResultValue, null);
            return new Quantity().withValue(resultValue).withUnit(resultUnit);
        } else if (left instanceof BigDecimal leftBigDecimal && right instanceof Quantity rightQuantity) {
            BigDecimal value = Value.verifyPrecision(leftBigDecimal.multiply(rightQuantity.getValue()), null);
            return rightQuantity.withValue(value);
        } else if (left instanceof Quantity leftQuantity && right instanceof BigDecimal rightBigDecimal) {
            BigDecimal value = Value.verifyPrecision((leftQuantity.getValue()).multiply(rightBigDecimal), null);
            return leftQuantity.withValue(value);
        } else if (left instanceof Interval leftInterval && right instanceof Interval rightInterval) {
            return new Interval(
                    multiply(leftInterval.getStart(), rightInterval.getStart(), state),
                    true,
                    multiply(leftInterval.getEnd(), rightInterval.getEnd(), state),
                    true,
                    state);
        }

        throw new InvalidOperatorArgument(
                "Multiply(Integer, Integer), Multiply(Long, Long), Multiply(Decimal, Decimal), Multiply(Decimal, Quantity), Multiply(Quantity, Decimal) or Multiply(Quantity, Quantity)",
                String.format(
                        "Multiply(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
