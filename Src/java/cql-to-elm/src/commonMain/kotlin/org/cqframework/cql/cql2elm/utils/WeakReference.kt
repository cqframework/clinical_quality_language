package org.cqframework.cql.cql2elm.utils

expect class WeakReference<T>(value: T) {
    fun get(): T?
}