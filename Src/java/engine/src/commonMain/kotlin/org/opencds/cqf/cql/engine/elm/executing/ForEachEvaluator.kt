package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlList

object ForEachEvaluator {
    @JvmStatic
    fun forEach(source: Value?, element: Value?): List? {
        if (source == null || element == null) {
            return null
        }

        return (source as List)
            .map { PropertyEvaluator.resolvePath(it, (element as String).value) }
            .toCqlList()
    }
}
