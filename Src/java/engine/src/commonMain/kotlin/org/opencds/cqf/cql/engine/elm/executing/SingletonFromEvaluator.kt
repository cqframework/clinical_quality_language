package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
singleton from(argument List<T>) T

The singleton from operator extracts a single element from the source list.
If the source list is empty, the result is null.
If the source list contains one element, that element is returned.
If the list contains more than one element, a run-time error is thrown.
If the source list is null, the result is null.
*/
object SingletonFromEvaluator {
    @JvmStatic
    fun singletonFrom(operand: Any?): Any? {
        if (operand == null) {
            return null
        }

        var result: Any? = null
        var first = true
        if (operand is Iterable<*>) {
            for (element in operand) {
                if (first) {
                    result = element
                    first = false
                } else {
                    throw InvalidOperatorArgument(
                        "Expected a list with at most one element, but found a list with multiple elements."
                    )
                }
            }
            return result
        }

        throw InvalidOperatorArgument(
            "SingletonFrom(List<T>)",
            "SingletonFrom(${operand.javaClassName})",
        )
    }
}
