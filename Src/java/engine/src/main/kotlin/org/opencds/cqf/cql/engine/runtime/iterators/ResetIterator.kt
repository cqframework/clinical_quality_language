package org.opencds.cqf.cql.engine.runtime.iterators

/** Created by Bryn on 8/11/2019. */
class ResetIterator<E>(private val source: Iterator<E?>) : Iterator<E?> {
    private var data = ArrayList<E?>()
    var dataIndex: Int = -1
    private var dataCached = false

    init {
        data = ArrayList<E?>()
    }

    override fun hasNext(): Boolean {
        if (!dataCached) {
            return source.hasNext()
        }

        return dataIndex < data.size - 1 && data.isNotEmpty()
    }

    override fun next(): E? {
        if (!dataCached) {
            val element = source.next()
            data.add(element)
            return element
        }

        dataIndex++
        return data[dataIndex]
    }

    fun reset() {
        // Fill any remaining data
        while (source.hasNext()) {
            data.add(source.next())
        }
        dataCached = true
        dataIndex = -1
    }
}
