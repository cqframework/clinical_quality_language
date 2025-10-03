package org.opencds.cqf.cql.engine.runtime.iterators

import org.opencds.cqf.cql.engine.execution.State

/** Created by Bryn on 8/11/2019. */
class QueryIterator(state: State?, sources: MutableList<Iterator<Any?>>) : Iterator<Any?> {
    private var sourceIterator: Iterator<Any?>? = null
    private val result = mutableListOf<Any?>(sources.size)

    init {

        for (i in sources.indices.reversed()) {
            if (sourceIterator == null) {
                sourceIterator = sources[i]
            } else {
                sourceIterator = TimesIterator(sources[i], sourceIterator!!)
            }
            result.add(null)
        }
    }

    override fun hasNext(): Boolean {
        return sourceIterator!!.hasNext()
    }

    override fun next(): Any {
        return unpack(sourceIterator!!.next())
    }

    private fun unpack(element: Any?): Any {
        unpair(element, result, 0)
        return result
    }

    private fun unpair(element: Any?, target: MutableList<Any?>, index: Int) {
        if (element is MutableMap.MutableEntry<*, *>) {
            unpair(element.key, target, index)
            unpair(element.value, target, index + 1)
        } else {
            target[index] = element
        }
    }
}
