package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.execution.State

object ForEachEvaluator {
    @JvmStatic
    fun forEach(source: Any?, element: Any?, state: State?): Any? {
        if (source == null || element == null) {
            return null
        }

        val retVal: MutableList<Any?> = ArrayList<Any?>()
        for (o in source as Iterable<*>) {
            retVal.add(state!!.environment.resolvePath(o, element.toString()))
        }
        return retVal
    }
}
