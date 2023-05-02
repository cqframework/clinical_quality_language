package org.opencds.cqf.cql.engine.elm.visiting;


import java.util.List;

/*
Coalesce<T>(argument1 T, argument2 T) T
Coalesce<T>(argument1 T, argument2 T, argument3 T) T
Coalesce<T>(argument1 T, argument2 T, argument3 T, argument4 T) T
Coalesce<T>(argument1 T, argument2 T, argument3 T, argument4 T, argument5 T) T
Coalesce<T>(arguments List<T>) T

The Coalesce operator returns the first non-null result in a list of arguments.
If all arguments evaluate to null, the result is null.
The static type of the first argument determines the type of the result, and all subsequent arguments must be of that same type.
*/

public class CoalesceEvaluator {

    public static Object coalesce(List<Object> operands) {
        for (Object operand : operands) {

            if (operand != null) {

                if (operand instanceof Iterable && operands.size() == 1) {

                    for (Object obj : ((Iterable<?>) operand)) {
                        if (obj != null) {
                            return obj;
                        }
                    }
                    return null;
                }
                return operand;
            }
        }
        return null;
    }

}
