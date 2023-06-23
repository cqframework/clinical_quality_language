package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;

import java.math.BigDecimal;

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

public class ConvertsToQuantityEvaluator {

    public static Boolean convertsToQuantity(Object argument, State state) {
        if (argument == null) {
            return null;
        }

        if (argument instanceof Quantity) {
            return true;
        }

        if (argument instanceof String || argument instanceof Ratio || argument instanceof BigDecimal || argument instanceof Integer)
        {
            try {
                Object response = ToQuantityEvaluator.toQuantity(argument, state);
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

}
