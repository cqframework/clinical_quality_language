package org.cqframework.cql.cql2elm.utils

import kotlin.js.asDynamic
import kotlin.js.collections.JsMap

internal actual class IdentityHashMap<K : Any, V : Any> {
    @OptIn(ExperimentalJsCollectionsApi::class) private val backingMap = JsMap<K, V>().asDynamic()

    actual operator fun get(key: K): V? {
        return backingMap.get(key)
    }

    actual operator fun set(key: K, value: V) {
        backingMap.set(key, value)
    }

    actual operator fun iterator(): Iterator<Map.Entry<K, V>> {
        @Suppress("IteratorHasNextCallsNextMethod")
        return object : Iterator<Map.Entry<K, V>> {
            private var iterator = backingMap.entries()
            private var nextResult: dynamic? = null

            override fun hasNext(): Boolean {
                if (nextResult == null) {
                    nextResult = iterator.next()
                }
                return !nextResult.done
            }

            override fun next(): Map.Entry<K, V> {
                if (nextResult == null) {
                    nextResult = iterator.next()
                }
                if (nextResult.done) {
                    throw NoSuchElementException()
                }
                val entry = nextResult.value
                nextResult = null
                return object : Map.Entry<K, V> {
                    override val key = entry[0]
                    override val value = entry[1]
                }
            }
        }
    }

    actual fun getOrPut(key: K, defaultValue: () -> V): V {
        val existingValue = this[key]
        if (existingValue != null) {
            return existingValue
        }
        val newValue = defaultValue()
        this[key] = newValue
        return newValue
    }
}
