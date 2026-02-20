@file:OptIn(ExperimentalWasmJsInterop::class)

package org.cqframework.cql.cql2elm.utils

@JsName("Map")
private external class JsMap {
    fun get(key: JsAny): JsAny?

    fun set(key: JsAny, value: JsAny)

    fun entries(): JsMapIterator
}

private external interface JsMapIterator {
    fun next(): JsMapIteratorResult
}

private external interface JsMapIteratorResult {
    val done: Boolean
    val value: JsArray<JsAny>?
}

internal actual class IdentityHashMap<K : Any, V : Any> {
    private val backingMap = JsMap()

    actual operator fun get(key: K): V? {
        return backingMap.get(key.toJsReference())?.unsafeCast<JsReference<V>>()?.get()
    }

    actual operator fun set(key: K, value: V) {
        backingMap.set(key.toJsReference(), value.toJsReference())
    }

    actual operator fun iterator(): Iterator<Map.Entry<K, V>> {
        return object : Iterator<Map.Entry<K, V>> {
            private var iterator = backingMap.entries()
            private var nextResult: JsMapIteratorResult? = null

            override fun hasNext(): Boolean {
                if (nextResult == null) {
                    nextResult = iterator.next()
                }
                return !nextResult!!.done
            }

            override fun next(): Map.Entry<K, V> {
                if (nextResult == null) {
                    nextResult = iterator.next()
                }
                if (nextResult!!.done) {
                    throw NoSuchElementException()
                }
                val entry = nextResult!!.value!!
                nextResult = null
                return object : Map.Entry<K, V> {
                    override val key = entry.get(0)!!.unsafeCast<JsReference<K>>().get()
                    override val value = entry.get(1)!!.unsafeCast<JsReference<V>>().get()
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
