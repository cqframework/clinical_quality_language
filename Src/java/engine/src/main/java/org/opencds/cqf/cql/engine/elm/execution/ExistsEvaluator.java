package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.CqlList;

/*
exists(argument List<T>) Boolean

The exists operator returns true if the list contains any non-null elements.
If the argument is null, the result is null.
*/

public class ExistsEvaluator extends org.cqframework.cql.elm.execution.Exists {

    @SuppressWarnings("unchecked")
    public static Object exists(Object operand) {
        Iterable<Object> value = (Iterable<Object>) operand;

        if (value == null) {
            return false;
        }

        return !CqlList.toList(value, false).isEmpty();
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);

        return exists(operand);
    }
}
