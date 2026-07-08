package org.cqframework.cql.cql2elm.utils

/**
 * This is a map that uses weak references for keys. This means that if the key is no longer
 * strongly referenced anywhere in the program, it will be garbage collected and the entry will be
 * removed from the map. This is useful for extension properties. The lifetime of the property is
 * tied to the lifetime of the object. In non-Java environments, this is implemented as an
 * IdentityHashMap.
 */
internal expect class WeakIdentityHashMap<K : Any, V : Any>() {
    fun getOrPut(key: K, defaultValue: () -> V): V
}
