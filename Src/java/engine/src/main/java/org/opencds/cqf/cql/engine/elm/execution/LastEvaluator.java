package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;

/*
Last(argument List<T>) T

The Last operator returns the last element in a list. In a list of length N, the operator
  is equivalent to invoking the indexer with an index of N - 1.
If the argument is null, the result is null.
*/

public class LastEvaluator extends org.cqframework.cql.elm.execution.Last {

    public static Object last(Object source) {
        if (source == null) {
            return null;
        }

        Object result = null;
        for (Object element : (Iterable<?>) source) {
            result = element;
        }

        return result;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);

        return last(source);
    }
}
