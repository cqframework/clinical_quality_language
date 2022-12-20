package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*
Ceiling(argument Decimal) Integer

The Ceiling operator returns the first integer greater than or equal to the argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/

public class CeilingEvaluator extends org.cqframework.cql.elm.execution.Ceiling {

    public static Object ceiling(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof BigDecimal) {
            return BigDecimal.valueOf(Math.ceil(((BigDecimal)operand).doubleValue())).intValue();
        }

        else if (operand instanceof Quantity) {
            return BigDecimal.valueOf(Math.ceil(((Quantity)operand).getValue().doubleValue())).intValue();
        }

        throw new InvalidOperatorArgument("Ceiling(Decimal)", String.format("Ceiling(%s)", operand.getClass().getName()));
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return ceiling(operand);
    }
}
