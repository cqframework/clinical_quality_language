package org.opencds.cqf.cql.engine.util

expect fun <K, V> createLinkedHashMap(
    initialCapacity: Int,
    loadFactor: Float,
    accessOrder: Boolean,
    sizeThreshold: Int,
): LinkedHashMap<K, V>
