package org.cqframework.cql.cql2elm.utils

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return mutableMapOf()
}
