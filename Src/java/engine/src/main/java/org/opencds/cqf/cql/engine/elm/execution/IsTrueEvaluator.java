package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
is true(argument Boolean) Boolean

The is true operator determines whether or not its argument evaluates to true.
If the argument evaluates to true, the result is true; otherwise, the result is false.
*/

public class IsTrueEvaluator extends org.cqframework.cql.elm.execution.IsTrue {

    public static Object isTrue(Boolean operand) {
        return Boolean.TRUE == operand;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Boolean operand = (Boolean) getOperand().evaluate(context);

        return isTrue(operand);
    }
}
