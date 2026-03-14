package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.util.javaClassName

/*
flatten(argument List<List<T>>) List<T>

The flatten operator flattens a list of lists into a single list.
*/
object FlattenEvaluator {
    @JvmStatic
    fun flatten(operand: Any?): List<Any?>? {
        if (operand == null) {
            return null
        }

        if (operand is Iterable<*>) {
            val resultList = mutableListOf<Any?>()
            for (element in operand) {
                if (element == null) {
                    resultList.add(null)
                } else {
                    for (subElement in element as Iterable<*>) {
                        resultList.add(subElement)
                    }
                }
            }

            return resultList
        }

        throw InvalidOperatorArgument("Flatten(List<List<T>>)", "Flatten(${operand.javaClassName})")
    }
}
