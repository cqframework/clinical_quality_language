package org.cqframework.cql.cql2elm.tracking

actual class WeakIdentityHashMap<K : Any, V> {
    private val backingMap = HashMap<K, V>()
    actual val size: Int
        get() = backingMap.size

    actual operator fun get(key: K): V? {
        return backingMap[key]
    }

    actual operator fun set(key: K, value: V) {
        backingMap[key] = value
    }

    actual fun remove(key: K): V? {
        return backingMap.remove(key)
    }

    actual fun getOrPut(key: K, defaultValue: () -> V): V {
        return backingMap.getOrPut(key, defaultValue)
    }
}
