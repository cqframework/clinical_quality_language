package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
Lower(argument String) String

The Lower operator returns the lower case of its argument.
If the argument is null, the result is null.
*/

public class LowerEvaluator extends org.cqframework.cql.elm.execution.Lower {

    public static Object lower(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            return ((String) operand).toLowerCase();
        }

        throw new InvalidOperatorArgument(
                "Lower(String)",
                String.format("Lower(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return lower(operand);
    }
}
