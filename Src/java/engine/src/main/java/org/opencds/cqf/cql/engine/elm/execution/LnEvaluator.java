package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
Ln(argument Decimal) Decimal

The Ln operator computes the natural logarithm of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/

public class LnEvaluator extends org.cqframework.cql.elm.execution.Ln {

    public static Object ln(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof BigDecimal) {
            BigDecimal retVal;
            try {
                retVal = new BigDecimal(Math.log(((BigDecimal) operand).doubleValue()));
            }
            catch (NumberFormatException nfe) {
                if (((BigDecimal) operand).compareTo(new BigDecimal(0)) < 0) {
                    return null;
                }

                else if (((BigDecimal) operand).compareTo(new BigDecimal(0)) == 0) {
                    throw new UndefinedResult("Results in negative infinity");
                }
                else {
                    throw new UndefinedResult(nfe.getMessage());
                }
            }
            return Value.verifyPrecision(retVal, null);
        }

        throw new InvalidOperatorArgument(
                "Ln(Decimal)",
                String.format("Ln(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);

        return ln(operand);
    }
}
