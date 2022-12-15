package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;

/*

    ConvertsToQuantity(argument Decimal) Boolean
    ConvertsToQuantity(argument Integer) Boolean
    ConvertsToQuantity(argument Ratio) Boolean
    ConvertsToQuantity(argument String) Boolean

    The ConvertsToQuantity operator returns true if its argument is or can be converted to a Quantity value. See the ToQuantity
        operator for a description of the supported conversions.

    If the input string is not formatted correctly, or cannot be interpreted as a valid Quantity value, the result is false.

    If the argument is null, the result is null.

*/

public class ConvertsToQuantityEvaluator extends org.cqframework.cql.elm.execution.ConvertsToQuantity {

    public static Boolean convertsToQuantity(Object argument, Context context) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Quantity) {
            return true;
        }

        if (argument instanceof String || argument instanceof Ratio || argument instanceof BigDecimal || argument instanceof Integer)
        {
            try {
                Object response = ToQuantityEvaluator.toQuantity(argument, context);
                if (response == null) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        throw new InvalidOperatorArgument(
                "ConvertsToQuantity(String) or ConvertsToQuantity(Ratio) or ConvertsToQuantity(Integer) or ConvertsToQuantity(Decimal)",
                String.format("ConvertsToQuantity(%s)", argument.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return convertsToQuantity(operand, context);
    }

}
