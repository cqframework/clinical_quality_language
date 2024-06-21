package org.opencds.cqf.cql.engine.elm.executing;

import java.math.BigDecimal;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.Value;

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
            double base = ((BigDecimal) right).doubleValue();
            double argument = ((BigDecimal) left).doubleValue();

            // Logarithm is not defined for base 1.
            if (base == 1) {
                return null;
            }

            return Value.verifyPrecision(BigDecimal.valueOf(Math.log(argument) / Math.log(base)), null);
        }

        throw new InvalidOperatorArgument(
                "Log(Decimal, Decimal)",
                String.format(
                        "Log(%s, %s)",
                        left.getClass().getName(), right.getClass().getName()));
    }
}
