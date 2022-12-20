package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Ratio;

/*

    ToRatio(argument String) Ratio

    The ToRatio operator converts the value of its argument to a Ratio value. The operator accepts strings using the following format:
    <quantity>:<quantity>

    where <quantity> is the format used to by the ToQuantity operator.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Ratio value, the result is null.

    If the argument is null, the result is null.

    The following examples illustrate the behavior of the ToRatio operator:

    define IsValid: ToRatio('1.0 \'mg\':2.0 \'mg\'')
    define IsNull: ToRatio('1.0 \'mg\';2.0 \'mg\'')

*/

public class ToRatioEvaluator extends org.cqframework.cql.elm.execution.ToRatio {

    public static Object toRatio(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Ratio) {
            return operand;
        }

        if (operand instanceof String) {
            String[] quantityStrings = ((String) operand).split(":");
            if (quantityStrings.length == 2) {
                return new Ratio()
                        .setNumerator(ToQuantityEvaluator.toQuantity(quantityStrings[0]))
                        .setDenominator(ToQuantityEvaluator.toQuantity(quantityStrings[1]));

            }
            return null;
        }

        throw new IllegalArgumentException(String.format("Cannot cast a value of type %s as Ratio - use String values.", operand.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return toRatio(operand);
    }
}
