package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
First(argument List<T>) T

The First operator returns the first element in a list. The operator is equivalent to invoking the indexer with an index of 0.
If the argument is null, the result is null.
*/
object FirstEvaluator {
    @JvmStatic
    fun first(source: CqlType?): CqlType? {
        if (source == null) {
            return null
        }

        if (source is List) {
            val iter = source.iterator()
            if (iter.hasNext()) {
                return iter.next()
            }
        }

        return null
    }
}
