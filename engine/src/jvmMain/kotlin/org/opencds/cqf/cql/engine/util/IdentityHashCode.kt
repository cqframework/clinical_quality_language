package org.opencds.cqf.cql.engine.util

actual fun identityHashCode(value: Any): Int {
    return System.identityHashCode(value)
}
