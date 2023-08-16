package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;

import java.math.BigDecimal;

/*
Exp(argument Decimal) Decimal

The Exp operator raises e to the power of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/

public class ExpEvaluator {

    public static Object exp(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof BigDecimal){
            try {
                return BigDecimal.valueOf(Math.exp(((BigDecimal)operand).doubleValue()));
            }
            catch (NumberFormatException nfe) {
                if (((BigDecimal)operand).compareTo(new BigDecimal(0)) > 0) {
                    throw new UndefinedResult("Results in positive infinity");
                }
                else if (((BigDecimal)operand).compareTo(new BigDecimal(0)) < 0) {
                    throw new UndefinedResult("Results in negative infinity");
                }
                else {
                    throw new UndefinedResult(nfe.getMessage());
                }
            }
        }

        throw new InvalidOperatorArgument(
                "Exp(Decimal)",
                String.format("Exp(%s)", operand.getClass().getName())
        );
    }

}
