package org.opencds.cqf.cql.engine.elm.executing

import org.opencds.cqf.cql.engine.execution.State

/*
distinct(argument List<T>) List<T>

The distinct operator returns the given list with duplicates eliminated using equality semantics.

If the argument is null, the result is null.
*/
object DistinctEvaluator {
    fun distinct(source: Iterable<*>?, state: State?): List<Any?>? {
        if (source == null) {
            return null
        }

        val result = mutableListOf<Any?>()
        for (element in source) {
            if (element == null && result.parallelStream().noneMatch { obj -> obj == null }) {
                result.add(null)
                continue
            }

            val `in` = InEvaluator.`in`(element, result, null, state) ?: continue

            if (!`in`) result.add(element)
        }

        return result
    }
}
