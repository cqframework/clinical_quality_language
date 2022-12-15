package org.opencds.cqf.cql.engine.elm.execution;

import java.util.Iterator;

import org.opencds.cqf.cql.engine.execution.Context;

/*
First(argument List<T>) T

The First operator returns the first element in a list. The operator is equivalent to invoking the indexer with an index of 0.
If the argument is null, the result is null.
*/

public class FirstEvaluator extends org.cqframework.cql.elm.execution.First {

    public static Object first(Object source) {

        if (source == null) {
            return null;
        }

        if (source instanceof Iterable) {
            Iterator<?> iter = ((Iterable<?>)source).iterator();
            if (iter.hasNext()) {
                return iter.next();
            }
        }

        return null;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);

        return first(source);
    }
}
