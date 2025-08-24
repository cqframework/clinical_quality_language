package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.fhir.ucum.Decimal;
import org.fhir.ucum.Pair;
import org.fhir.ucum.UcumException;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
/(left Decimal, right Decimal) Decimal
/(left Quantity, right Decimal) Quantity
/(left Quantity, right Quantity) Quantity

The divide (/) operator performs numeric division of its arguments.
Note that this operator is Decimal division; for Integer division, use the truncated divide (div) operator.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
For division operations involving quantities, the resulting quantity will have the appropriate unit. For example:
12 'cm2' / 3 'cm'
In this example, the result will have a unit of 'cm'.
If either argument is null, the result is null.
*/

public class DivideEvaluator {

    private static BigDecimal divideHelper(BigDecimal left, BigDecimal right, State state) {
        if (EqualEvaluator.equal(right, new BigDecimal("0.0"), state)) {
            return null;
        }

        try {
            return Value.verifyPrecision(left.divide(right), null);
        } catch (ArithmeticException e) {
            return left.divide(right, 8, RoundingMode.FLOOR);
        }
    }

    public static Object divide(Object left, Object right, State state) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof BigDecimal leftBigDecimal && right instanceof BigDecimal rightBigDecimal) {
            return divideHelper(leftBigDecimal, rightBigDecimal, state);
        } else if (left instanceof Quantity leftQuantity && right instanceof Quantity rightQuantity) {
            final var leftValue = leftQuantity.getValue();
            final var leftUnit = leftQuantity.getUnit();
            final var rightValue = rightQuantity.getValue();
            final var rightUnit = rightQuantity.getUnit();
            if (EqualEvaluator.equal(rightValue, new BigDecimal("0.0"), state)) {
                return null;
            }
            final BigDecimal resultValue;
            final String resultUnit;
            if (rightUnit.equals("1")) {
                resultValue = divideHelper(leftValue, rightValue, state);
                resultUnit = leftUnit;
            } else {
                final var ucumService =
                        state.getEnvironment().getLibraryManager().getUcumService();
                try {
                    final var result = ucumService.divideBy(
                            new Pair(new Decimal(String.valueOf(leftValue)), leftUnit),
                            new Pair(new Decimal(String.valueOf(rightValue)), rightUnit));
                    final var unverifiedResultValue =
                            new BigDecimal(result.getValue().asDecimal());
                    resultValue = Value.verifyPrecision(unverifiedResultValue, null);
                    final var rawResultUnit = result.getCode();
                    resultUnit = rawResultUnit.isEmpty() ? "1" : rawResultUnit;
                } catch (UcumException e) {
                    throw new RuntimeException(e);
                }
            }
            return new Quantity().withValue(resultValue).withUnit(resultUnit);
        } else if (left instanceof Quantity && right instanceof BigDecimal) {
            BigDecimal value = divideHelper(((Quantity) left).getValue(), (BigDecimal) right, state);
            if (value == null) {
                return null;
            }
            return new Quantity().withValue(value).withUnit(((Quantity) left).getUnit());
        } else if (left instanceof Interval leftInterval && right instanceof Interval rightInterval) {
            return new Interval(
                    divide(leftInterval.getStart(), rightInterval.getStart(), state),
                    true,
                    divide(leftInterval.getEnd(), rightInterval.getEnd(), state),
                    true,
                    state);
        }

        throw new InvalidOperatorArgument(
                "Divide(Decimal, Decimal), Divide(Quantity, Decimal), Divide(Quantity, Quantity)",
                String.format(
                        "Divide(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
