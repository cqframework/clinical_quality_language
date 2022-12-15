package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Collections;

import org.opencds.cqf.cql.engine.execution.Context;

public class ToListEvaluator extends org.cqframework.cql.elm.execution.ToList {

    public static Object toList(Object operand)
    {
        // check to see if it is already a list
        if (operand instanceof Iterable)
        {
            return operand;
        }

        return operand == null ? Collections.emptyList() : Collections.singletonList(operand);
    }

    @Override
    protected Object internalEvaluate(Context context)
    {
        return toList(getOperand().evaluate(context));
    }
}
