package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Value;

/*

ToDecimal(argument String) Decimal

The ToDecimal operator converts the value of its argument to a Decimal value.
The operator accepts strings using the following format:
  (+|-)?#0(.0#)?
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit,
  followed optionally by a decimal point, at least one digit, and any number of additional digits (including none).
Note that the decimal value returned by this operator must be limited in precision and scale to the maximum precision and
  scale representable for Decimal values within CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid Decimal value, the result is null.
If the argument is null, the result is null.

*/

public class ToDecimalEvaluator extends org.cqframework.cql.elm.execution.ToDecimal {

    public static Object toDecimal(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Boolean) {
            return ((Boolean)operand) ? new BigDecimal("1.0") : new BigDecimal("0.0");
        }

        if (operand instanceof BigDecimal) {
            return operand;
        }

        if (operand instanceof Integer) {
            return new BigDecimal((Integer)operand);
        }

        if (operand instanceof Long) {
            return new BigDecimal((Long)operand);
        }

        if (operand instanceof String) {
            try {
                if (((String) operand).contains(".")) {
                    String[] decimalSplit = ((String) operand).split("\\.");
                    if ((decimalSplit[0].contains("-") || decimalSplit[0].contains("+")) && decimalSplit[0].length() == 1) {
                        return null;
                    }
                    else if (decimalSplit[0].length() == 0) {
                        return null;
                    }
                }
                return Value.validateDecimal(new BigDecimal((String)operand), null);
            }
            catch (NumberFormatException nfe) {
                return null;
            }
        }

        throw new InvalidOperatorArgument(
                "ToDecimal(String)",
                String.format("ToDecimal(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return toDecimal(operand);
    }
}
