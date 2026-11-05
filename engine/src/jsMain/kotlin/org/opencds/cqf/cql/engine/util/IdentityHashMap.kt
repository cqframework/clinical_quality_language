package org.opencds.cqf.cql.engine.util

/**
 * [mutableMapOf] would compare keys with [equals], which is the opposite of what an identity map
 * promises: two structurally equal but distinct objects would collapse into one entry. See
 * [IdentityKeyedMap].
 */
actual fun <K, V> createIdentityHashMap(): MutableMap<K, V> {
    return IdentityKeyedMap()
}
