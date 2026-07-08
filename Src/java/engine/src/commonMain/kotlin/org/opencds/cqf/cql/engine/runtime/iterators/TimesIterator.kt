package org.opencds.cqf.cql.engine.runtime.iterators

/** Created by Bryn on 8/11/2019. */
class TimesIterator(private val left: Iterator<Any?>, right: Iterator<Any?>) : Iterator<Any?> {
    private val right = ResetIterator<Any?>(right)
    private var leftNeeded = true
    private var leftElement: Any? = null

    override fun hasNext(): Boolean {
        if (leftNeeded) {
            return left.hasNext() && right.hasNext()
        }

        if (!right.hasNext()) {
            if (left.hasNext()) {
                right.reset()
                leftNeeded = true
            }
        }

        return right.hasNext()
    }

    override fun next(): Any {
        if (leftNeeded) {
            leftElement = left.next()
            leftNeeded = false
        }

        return mutableMapOf(leftElement to right.next()).entries.first()
    }
}
