package org.cqframework.cql.cql2elm.tracking

import org.cqframework.cql.cql2elm.utils.WeakReference


/**
 * This is a map that uses weak references for keys. This means that if the key is no longer
 * strongly referenced anywhere in the program, it will be garbage collected and the entry will be
 * removed from the map. This is useful for extension properties. The lifetime of the property is
 * tied to the lifetime of the object
 */
internal class WeakIdentityHashMap<K : Any, V> {
    companion object {
        const val OPERATION_CLEANUP_INTERVAL = 5000
    }

    private val backingMap = HashMap<WeakKey<K>, V>()
    private var operationCount = 0

    val size: Int
        get() = backingMap.size

    operator fun get(key: K): V? {
        incrementAndCleanUp()
        return backingMap[WeakKey(key)]
    }

    operator fun set(key: K, value: V) {
        incrementAndCleanUp()
        backingMap[WeakKey(key)] = value
    }

    fun remove(key: K): V? {
        incrementAndCleanUp()
        return backingMap.remove(WeakKey(key))
    }

    fun getOrPut(key: K, defaultValue: () -> V): V {
        incrementAndCleanUp()
        return backingMap.getOrPut(WeakKey(key), defaultValue)
    }

    private fun incrementAndCleanUp() {
        operationCount++
        if (operationCount >= OPERATION_CLEANUP_INTERVAL) {
            cleanUp()
            operationCount = 0
        }
    }

    private fun cleanUp() {
        val keys = backingMap.keys.filter { it.get() == null }
        keys.forEach { backingMap.remove(it) }
    }

    private class WeakKey<K : Any>(key: K) {
        private val ref = WeakReference(key)
        private val hashCode = key.hashCode()

        fun get(): K? = ref.get()

        override fun equals(other: Any?): Boolean {
            if (other !is WeakKey<*> || this.hashCode != other.hashCode) return false
            // Compare by reference for the actual object.
            // We want to do this as rarely as is feasible because the
            // direct access resets garbage collection.
            return this.ref.get() === other.ref.get()
        }

        override fun hashCode(): Int = hashCode
    }
}
