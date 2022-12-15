package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
mod(left Integer, right Integer) Integer
mod(left Long, right Long) Long
mod(left Decimal, right Decimal) Decimal
mod(left Quantity, right Quantity) Quantity

The mod operator computes the remainder of the division of its arguments.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/

public class ModuloEvaluator extends org.cqframework.cql.elm.execution.Modulo {

    public static Object modulo(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer) {
            if ((Integer)right == 0) {
                return null;
            }
            return (Integer)left % (Integer)right;
        }

        if (left instanceof Long) {
            if ((Long)right == 0L) {
                return null;
            }
            return (Long)left % (Long)right;
        }

        if (left instanceof BigDecimal) {
            if (right == new BigDecimal("0.0")) {
                return null;
            }
            return ((BigDecimal)left).remainder((BigDecimal)right).setScale(8, RoundingMode.FLOOR);
        }

        if (left instanceof Quantity) {
            if (((Quantity) right).getValue().compareTo(new BigDecimal("0.0")) == 0) {
                return null;
            }

            return new Quantity()
                .withUnit(((Quantity) left).getUnit())
                .withValue(((Quantity) left).getValue().
                    remainder(((Quantity) right).getValue()).setScale(8, RoundingMode.FLOOR));
        }

        throw new InvalidOperatorArgument(
                "Modulo(Integer, Integer), Modulo(Long, Long) or Modulo(Decimal, Decimal), , Modulo(Quantity, Quantity)",
                String.format("Modulo(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return modulo(left, right);
    }
}
