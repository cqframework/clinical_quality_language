package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

/*
distinct(argument List<T>) List<T>

The distinct operator returns the given list with duplicates eliminated using equality semantics.

If the argument is null, the result is null.
*/
object DistinctEvaluator {
    fun distinct(source: Value?, state: State?): List? {
        if (source == null) {
            return null
        }

        if (source is List) {
            val result = mutableListOf<Value?>()
            for (element in source) {
                if (element == null && result.none { obj -> obj == null }) {
                    result.add(null)
                    continue
                }

                val `in` = InEvaluator.`in`(element, result.toCqlList(), null, state) ?: continue

                if (!`in`.value) result.add(element)
            }

            return result.toCqlList()
        }

        throw InvalidOperatorArgument("distinct(List<T>)", "distinct(${source.typeAsString})")
    }
}
