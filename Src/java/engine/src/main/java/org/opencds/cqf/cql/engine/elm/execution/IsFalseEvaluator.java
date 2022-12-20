package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
is false(argument Boolean) Boolean

The is false operator determines whether or not its argument evaluates to false.
If the argument evaluates to false, the result is true; otherwise, the result is false.
*/

public class IsFalseEvaluator extends org.cqframework.cql.elm.execution.IsFalse {

    public static Object isFalse(Boolean operand) {
        return Boolean.FALSE == operand;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Boolean operand = (Boolean) getOperand().evaluate(context);

        return isFalse(operand);
    }
}
