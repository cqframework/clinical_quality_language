package org.cqframework.cql.cql2elm.utils

actual class WeakReference<T> actual constructor(val value: T) {
    actual fun get(): T? {
        return value
    }
}