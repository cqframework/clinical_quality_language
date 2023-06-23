package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
/(left Decimal, right Decimal) Decimal
/(left Quantity, right Decimal) Quantity
/(left Quantity, right Quantity) Quantity

The divide (/) operator performs numeric division of its arguments.
Note that this operator is Decimal division; for Integer division, use the truncated divide (div) operator.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
TODO: For division operations involving quantities, the resulting quantity will have the appropriate unit. For example:
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

        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return divideHelper((BigDecimal) left, (BigDecimal) right, state);
        }

        else if (left instanceof Quantity && right instanceof Quantity) {
            BigDecimal value = divideHelper(((Quantity) left).getValue(), ((Quantity) right).getValue(), state);
            return new Quantity().withValue(Value.verifyPrecision(value, null)).withUnit(((Quantity) left).getUnit());
        }

        else if (left instanceof Quantity && right instanceof BigDecimal) {
            BigDecimal value = divideHelper(((Quantity) left).getValue(), (BigDecimal) right, state);
            return new Quantity().withValue(Value.verifyPrecision(value, null)).withUnit(((Quantity)left).getUnit());
        }

        else if (left instanceof Interval && right instanceof Interval) {
            Interval leftInterval = (Interval)left;
            Interval rightInterval = (Interval)right;

            return new Interval(
                    divide(leftInterval.getStart(), rightInterval.getStart(), state), true,
                    divide(leftInterval.getEnd(), rightInterval.getEnd(), state), true
            );
        }

        throw new InvalidOperatorArgument(
                "Divide(Decimal, Decimal), Divide(Quantity, Decimal), Divide(Quantity, Quantity)",
                String.format("Divide(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }
}
