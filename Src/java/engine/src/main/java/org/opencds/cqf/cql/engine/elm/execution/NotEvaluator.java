package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
not (argument Boolean) Boolean

The not operator returns true if the argument is false and false if the argument is true. Otherwise, the result is null.
*/

public class NotEvaluator extends org.cqframework.cql.elm.execution.Not {

    public static Boolean not(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof Boolean) {
            return !(Boolean) operand;
        }

        throw new InvalidOperatorArgument(
                "Not(Boolean)",
                String.format("Not(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return not(operand);
    }
}
