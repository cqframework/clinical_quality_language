package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Value;

import java.math.BigDecimal;

/*
Log(argument Decimal, base Decimal) Decimal

The Log operator computes the logarithm of its first argument, using the second argument as the base.
When invoked with Integer arguments, the arguments will be implicitly converted to Decimal.
If either argument is null, the result is null.
*/

public class LogEvaluator {

    public static Object log(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if (left instanceof BigDecimal) {
            Double base = Math.log(((BigDecimal)right).doubleValue());
            Double value = Math.log(((BigDecimal)left).doubleValue());

            if (base == 0) {
                return Value.verifyPrecision(new BigDecimal(value), null);
            }

            return Value.verifyPrecision(new BigDecimal(value / base), null);
        }

        throw new InvalidOperatorArgument(
                "Log(Decimal, Decimal)",
                String.format("Log(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }

}
