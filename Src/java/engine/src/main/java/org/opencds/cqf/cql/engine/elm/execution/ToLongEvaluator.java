package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Value;

/*
ToLong(argument String) Long

The ToLong operator converts the value of its argument to an Long value.
The operator accepts strings using the following format:
  (+|-)?#0
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit.
Note that the integer value returned by this operator must be a valid value in the range representable for Long values in CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid long value, the result is null.
If the argument is null, the result is null.
*/

public class ToLongEvaluator extends org.cqframework.cql.elm.execution.ToLong {

    public static Object toLong(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Boolean) {
            return ((Boolean)operand) ? 1 : 0;
        }

        if (operand instanceof Integer) {
            return Long.valueOf((Integer)operand);
        }

        if (operand instanceof String) {
            try {
                return Long.parseLong((String) operand);
            }
            catch (NumberFormatException nfe) {
                try {
                    Double ret = Double.parseDouble((String) operand);
                    if (Value.validateLong(ret) == null) {
                        return null;
                    }
                    return ret.longValue();
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }

        throw new InvalidOperatorArgument(
                "ToLong(String)",
                String.format("ToLong(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return toLong(operand);
    }
}
