package org.opencds.cqf.cql.engine.util

actual fun <K, V> createConcurrentHashMap(): MutableMap<K, V> {
    return java.util.concurrent.ConcurrentHashMap()
}
