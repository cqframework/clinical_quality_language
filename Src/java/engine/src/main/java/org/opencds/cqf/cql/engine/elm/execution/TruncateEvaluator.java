package org.opencds.cqf.cql.engine.elm.execution;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
Truncate(argument Decimal) Integer

The Truncate operator returns the integer component of its argument.
When invoked with an Integer argument, the argument will be implicitly converted to Decimal.
If the argument is null, the result is null.
*/

public class TruncateEvaluator extends org.cqframework.cql.elm.execution.Truncate {

    public static Object truncate(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof BigDecimal) {
            Double val = ((BigDecimal) operand).doubleValue();
            if (val < 0){
                return ((BigDecimal) operand).setScale(0, RoundingMode.CEILING).intValue();
            }
            else {
                return ((BigDecimal) operand).setScale(0, RoundingMode.FLOOR).intValue();
            }
        }

        throw new InvalidOperatorArgument(
                "Truncate(Decimal)",
                String.format("Truncate(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return truncate(operand);
    }
}
