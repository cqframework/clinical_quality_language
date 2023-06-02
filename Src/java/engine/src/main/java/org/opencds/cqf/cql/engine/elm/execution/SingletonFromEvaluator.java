package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
singleton from(argument List<T>) T

The singleton from operator extracts a single element from the source list.
If the source list is empty, the result is null.
If the source list contains one element, that element is returned.
If the list contains more than one element, a run-time error is thrown.
If the source list is null, the result is null.
*/

public class SingletonFromEvaluator extends org.cqframework.cql.elm.execution.SingletonFrom {

    public static Object singletonFrom(Object operand) {
        if (operand == null) {
            return null;
        }

        Object result = null;
        boolean first = true;
        if (operand instanceof Iterable) {
            for (Object element : (Iterable<?>) operand) {
                if (first) {
                    result = element;
                    first = false;
                }
                else {
                    throw new InvalidOperatorArgument("Expected a list with at most one element, but found a list with multiple elements.");
                }
            }
            return result;
        }

        throw new InvalidOperatorArgument(
                "SingletonFrom(List<T>)",
                String.format("SingletonFrom(%s)", operand.getClass().getName())
        );
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return singletonFrom(operand);
    }
}
