package org.opencds.cqf.cql.engine.util

/**
 * There is no identity hash on this target. The value's own [hashCode] is consistent with reference
 * equality too, it just may cost more to compute. See [IdentityKeyedMap].
 */
actual fun identityHashCode(value: Any): Int {
    return value.hashCode()
}
