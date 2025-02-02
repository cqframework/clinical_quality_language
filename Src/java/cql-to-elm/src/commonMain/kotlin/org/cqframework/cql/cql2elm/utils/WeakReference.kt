package org.cqframework.cql.cql2elm.utils

internal expect class WeakReference<T>(value: T) {
    fun get(): T?
}