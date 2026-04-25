package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.List

/*
Last(argument List<T>) T

The Last operator returns the last element in a list. In a list of length N, the operator
 is equivalent to invoking the indexer with an index of N - 1.
If the argument is null, the result is null.
*/

object LastEvaluator {
    @JvmStatic
    fun last(source: CqlType?): CqlType? {
        if (source == null) {
            return null
        }

        return (source as List).lastOrNull()
    }
}
