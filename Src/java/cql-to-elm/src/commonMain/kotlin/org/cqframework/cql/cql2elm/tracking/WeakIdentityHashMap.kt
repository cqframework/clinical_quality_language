package org.cqframework.cql.cql2elm.tracking

/**
 * This is a map that uses weak references for keys. This means that if the key is no longer
 * strongly referenced anywhere in the program, it will be garbage collected and the entry will be
 * removed from the map. This is useful for extension properties. The lifetime of the property is
 * tied to the lifetime of the object
 */
expect class WeakIdentityHashMap<K : Any, V>() {
    val size: Int

    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    fun remove(key: K): V?

    fun getOrPut(key: K, defaultValue: () -> V): V
}
