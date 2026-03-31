package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic

object ForEachEvaluator {
    @JvmStatic
    fun forEach(source: Any?, element: Any?): Any? {
        if (source == null || element == null) {
            return null
        }

        return (source as Iterable<*>).map { PropertyEvaluator.resolvePath(it, element.toString()) }
    }
}
