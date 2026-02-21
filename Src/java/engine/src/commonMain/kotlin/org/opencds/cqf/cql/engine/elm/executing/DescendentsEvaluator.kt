package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Tuple

object DescendentsEvaluator {
    private val descendents = mutableListOf<Any?>()

    @JvmStatic
    fun descendents(source: Any?): Any? {
        if (source == null) {
            return null
        }

        return getDescendents(source)
    }

    fun getDescendents(source: Any?): Any? {
        if (source is Iterable<*>) {
            for (element in source) {
                descendents.add(getDescendents(element))
            }
        } else if (source is Tuple) {
            for (element in source.elements.values) {
                descendents.add(getDescendents(element))
            }
        } else if (source is Interval) {
            descendents.add(getDescendents(source.start))
            descendents.add(getDescendents(source.end))
        } else {
            descendents.add(source)
        }

        return descendents
    }
}
