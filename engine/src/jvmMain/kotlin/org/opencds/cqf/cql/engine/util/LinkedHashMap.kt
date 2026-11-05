package org.opencds.cqf.cql.engine.util

actual fun <K, V> createLinkedHashMap(
    initialCapacity: Int,
    loadFactor: Float,
    accessOrder: Boolean,
    sizeThreshold: Int,
): LinkedHashMap<K, V> {
    return object : LinkedHashMap<K, V>(initialCapacity, loadFactor, accessOrder) {
        override fun removeEldestEntry(eldestEntry: MutableMap.MutableEntry<K, V>?): Boolean {
            return size > sizeThreshold
        }
    }
}
