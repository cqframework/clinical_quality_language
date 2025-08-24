package org.cqframework.cql.cql2elm.utils

/** A multiplatform map that uses reference equality for keys. */
internal expect class IdentityHashMap<K : Any, V : Any>() {
    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    operator fun iterator(): Iterator<Map.Entry<K, V>>

    fun getOrPut(key: K, defaultValue: () -> V): V
}
