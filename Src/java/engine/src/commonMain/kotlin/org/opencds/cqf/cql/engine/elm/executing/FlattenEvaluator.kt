package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

/*
flatten(argument List<List<T>>) List<T>

The flatten operator flattens a list of lists into a single list.
*/
object FlattenEvaluator {
    @JvmStatic
    fun flatten(operand: Value?): List? {
        if (operand == null) {
            return null
        }

        if (operand is List) {
            val resultList = mutableListOf<Value?>()
            for (element in operand) {
                if (element == null) {
                    resultList.add(null)
                } else {
                    for (subElement in element as List) {
                        resultList.add(subElement)
                    }
                }
            }

            return resultList.toCqlList()
        }

        throw InvalidOperatorArgument("Flatten(List<List<T>>)", "Flatten(${operand.typeAsString})")
    }
}
