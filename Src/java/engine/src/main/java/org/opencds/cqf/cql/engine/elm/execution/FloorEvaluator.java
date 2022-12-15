package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
Floor(argument Decimal) Integer

The Floor operator returns the first integer less than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/

public class FloorEvaluator extends org.cqframework.cql.elm.execution.Floor {

    public static Object floor(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof BigDecimal) {
            return BigDecimal.valueOf(Math.floor(((BigDecimal) operand).doubleValue())).intValue();
        }

        else if (operand instanceof Quantity) {
            return BigDecimal.valueOf(Math.floor(((Quantity) operand).getValue().doubleValue())).intValue();
        }

        throw new InvalidOperatorArgument(
                "Floor(Decimal)",
                String.format("Floor(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return floor(operand);
    }
}
