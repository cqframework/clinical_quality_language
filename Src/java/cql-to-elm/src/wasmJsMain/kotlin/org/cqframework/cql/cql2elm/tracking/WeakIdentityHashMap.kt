package org.cqframework.cql.cql2elm.tracking

import kotlin.collections.getOrPut as ktGetOrPut

actual typealias WeakIdentityHashMap<K, V> = HashMap<K, V>

actual inline fun <K, V> WeakIdentityHashMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
    return this.ktGetOrPut(key, defaultValue)
}
