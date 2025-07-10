package org.cqframework.cql.cql2elm.tracking

/**
 * This is a map that uses weak references for keys. This means that if the key is no longer
 * strongly referenced anywhere in the program, it will be garbage collected and the entry will be
 * removed from the map. This is useful for extension properties. The lifetime of the property is
 * tied to the lifetime of the object. In non-Java environments, this is an alias for a regular
 * HashMap.
 */
expect class WeakIdentityHashMap<K, V>() {
    val size: Int

    operator fun get(key: K): V?

    fun remove(key: K): V?
}

expect fun <K, V> WeakIdentityHashMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V
