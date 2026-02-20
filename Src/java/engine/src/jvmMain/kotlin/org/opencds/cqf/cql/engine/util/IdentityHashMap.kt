package org.opencds.cqf.cql.engine.util

actual fun <K, V> createIdentityHashMap(): MutableMap<K, V> {
    return java.util.IdentityHashMap()
}
