package org.cqframework.cql.cql2elm.utils

import java.util.concurrent.ConcurrentHashMap

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return ConcurrentHashMap()
}
