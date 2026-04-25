package org.opencds.cqf.cql.engine.elm.executing

import kotlin.jvm.JvmStatic
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.toCqlList

object DescendentsEvaluator {

    @JvmStatic
    fun descendents(source: CqlType?): List? {
        if (source == null) {
            return null
        }

        return getDescendents(source)
    }

    fun getDescendents(source: CqlType?): List {
        val descendents = mutableListOf<CqlType?>()

        if (source is List) {
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

        return descendents.toCqlList()
    }
}
