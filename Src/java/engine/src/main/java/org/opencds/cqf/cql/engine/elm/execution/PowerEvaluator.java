package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
^(argument Integer, exponent Integer) Integer
^(argument Decimal, exponent Decimal) Decimal

The power (^) operator raises the first argument to the power given by the second argument.
When invoked with mixed Integer and Decimal arguments, the Integer argument will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/

public class PowerEvaluator extends org.cqframework.cql.elm.execution.Power {

    public static Object power(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer) {
            if ((Integer)right < 0) {
                return new BigDecimal(1).divide(new BigDecimal((Integer)left).pow(Math.abs((Integer)right)));
            }
            return new BigDecimal((Integer)left).pow((Integer)right).intValue();
        }

        if (left instanceof Long) {
            if ((Long) right < 0) {
                return new BigDecimal(1).divide(new BigDecimal((Long) left).pow(Math.abs((Integer) right)));
            }

            return new BigDecimal((Long) left).pow((Integer) ((Long) right).intValue()).longValue();
        }

        if (left instanceof BigDecimal) {
            return Value.verifyPrecision(new BigDecimal(Math.pow((((BigDecimal)left).doubleValue()), ((BigDecimal)right).doubleValue())), null);
        }

        throw new InvalidOperatorArgument(
                "Power(Integer, Integer), Power(Long, Long) or Power(Decimal, Decimal)",
                String.format("Power(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        return power(left, right);
    }
}
