package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Quantity;

import java.math.BigDecimal;

/*
Abs(argument Integer) Integer
Abs(argument Long) Integer
Abs(argument Decimal) Decimal
Abs(argument Quantity) Quantity

The Abs operator returns the absolute value of its argument.
When taking the absolute value of a quantity, the unit is unchanged.
If the argument is null, the result is null.
*/

public class AbsEvaluator {

    public static Object abs(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Integer) {
            return  Math.abs((Integer)operand);
        }

        else if (operand instanceof Long) {
            return Math.abs((Long) operand);
        }

        else if (operand instanceof BigDecimal) {
            return ((BigDecimal)operand).abs();
        }

        else if (operand instanceof Quantity) {
            return new Quantity().withValue((((Quantity)operand).getValue()).abs()).withUnit(((Quantity)operand).getUnit());
        }

        throw new InvalidOperatorArgument(
            "Abs(Integer), Abs(Long), Abs(Decimal) or Abs(Quantity)",
            String.format("Abs(%s)", operand.getClass().getName())
        );
    }
}