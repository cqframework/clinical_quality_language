package org.opencds.cqf.cql.engine.util

actual fun <K, V> createLinkedHashMap(
    initialCapacity: Int,
    loadFactor: Float,
    accessOrder: Boolean,
    sizeThreshold: Int,
): LinkedHashMap<K, V> {
    return LinkedHashMap(initialCapacity, loadFactor)
}
