package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

import java.math.BigDecimal;

/*
ToBoolean(argument String) Boolean

The ToBoolean operator converts the value of its argument to a Boolean value.
The operator accepts the following string representations:
true: true t yes y 1
false: false f no n 0
Note that the operator will ignore case when interpreting the string as a Boolean value.
If the input cannot be interpreted as a valid Boolean value, the result is null.
If the argument is null, the result is null.
*/

public class ToBooleanEvaluator extends org.cqframework.cql.elm.execution.ToBoolean {

    public static Object toBoolean(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Boolean) {
            return operand;
        }

        if (operand instanceof Integer) {
            if (((Integer)operand) == 1) {
                return true;
            }
            if (((Integer)operand) == 0) {
                return false;
            }

            return null;
        }

        if (operand instanceof BigDecimal) {
            if (((BigDecimal)operand).compareTo(new BigDecimal("0.0")) == 0) {
                return false;
            }

            if (((BigDecimal)operand).compareTo(new BigDecimal("1.0")) == 0) {
                return true;
            }

            return null;
        }

        if (operand instanceof String) {
            String compare = ((String) operand).toLowerCase();
            if (compare.equals("true") || compare.equals("t")
                    || compare.equals("yes") || compare.equals("y") || compare.equals("1"))
            {
                return true;
            }
            else if (compare.equals("false") || compare.equals("f")
                    || compare.equals("no") || compare.equals("n") || compare.equals("0"))
            {
                return false;
            }

            return null;
        }

        throw new InvalidOperatorArgument(
                "ToBoolean(String)",
                String.format("ToBoolean(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return toBoolean(operand);
    }
}
