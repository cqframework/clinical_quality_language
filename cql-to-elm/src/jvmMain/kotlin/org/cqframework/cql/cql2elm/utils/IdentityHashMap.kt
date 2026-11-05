package org.cqframework.cql.cql2elm.utils

import java.util.IdentityHashMap as JavaIdentityHashMap

internal actual class IdentityHashMap<K : Any, V : Any> {
    private val backingMap = JavaIdentityHashMap<K, V>()

    actual operator fun get(key: K): V? {
        return backingMap[key]
    }

    actual operator fun set(key: K, value: V) {
        backingMap[key] = value
    }

    actual operator fun iterator(): Iterator<Map.Entry<K, V>> {
        return backingMap.entries.iterator()
    }

    actual fun getOrPut(key: K, defaultValue: () -> V): V {
        return backingMap.getOrPut(key, defaultValue)
    }
}
