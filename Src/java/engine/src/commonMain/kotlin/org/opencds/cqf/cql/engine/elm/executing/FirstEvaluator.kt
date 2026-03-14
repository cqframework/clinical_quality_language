package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic

/*
First(argument List<T>) T

The First operator returns the first element in a list. The operator is equivalent to invoking the indexer with an index of 0.
If the argument is null, the result is null.
*/
object FirstEvaluator {
    @JvmStatic
    fun first(source: Any?): Any? {
        if (source == null) {
            return null
        }

        if (source is Iterable<*>) {
            val iter = source.iterator()
            if (iter.hasNext()) {
                return iter.next()
            }
        }

        return null
    }
}
