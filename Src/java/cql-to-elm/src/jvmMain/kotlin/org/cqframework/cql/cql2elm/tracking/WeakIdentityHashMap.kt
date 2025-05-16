package org.cqframework.cql.cql2elm.tracking

import java.lang.ref.WeakReference

actual open class WeakIdentityHashMap<K, V> {
    companion object {
        const val OPERATION_CLEANUP_INTERVAL = 5000
    }

    private val backingMap = HashMap<WeakKey<K>, V>()
    private var operationCount = 0

    actual val size: Int
        get() = backingMap.size

    actual operator fun get(key: K): V? {
        incrementAndCleanUp()
        return backingMap[WeakKey(key)]
    }

    actual fun remove(key: K): V? {
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

    private class WeakKey<K>(key: K) {
        private val ref = WeakReference(key)
        private val hashCode = System.identityHashCode(key)

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

actual fun <K, V> WeakIdentityHashMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    return getOrPut(key, defaultValue)
}
