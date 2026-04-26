package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value

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
object CoalesceEvaluator {
    fun coalesce(operands: kotlin.collections.List<Value?>): Value? {
        for (operand in operands) {
            if (operand != null) {
                if (operand is List && operands.count() == 1) {
                    for (obj in operand) {
                        if (obj != null) {
                            return obj
                        }
                    }
                    return null
                }
                return operand
            }
        }
        return null
    }
}
