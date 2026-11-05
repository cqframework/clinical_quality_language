package org.opencds.cqf.cql.engine.util

/**
 * A [MutableMap] whose keys are compared by reference rather than by [equals].
 *
 * This is the portable stand-in for `java.util.IdentityHashMap` on targets that have no identity
 * map of their own. Keys are wrapped in a [Ref] whose equality is `===`; the wrapper hashes to the
 * key's own [hashCode], which keeps the hash consistent with that equality — the same object always
 * hashes the same — while letting two structurally equal but distinct objects sit side by side.
 *
 * Structurally equal keys therefore share a bucket and are told apart by reference comparison
 * within it. For the case this exists to serve — the handful of values a repeated retrieve produces
 * for one record — that bucket is small. It is also strictly less work than the [mutableMapOf] this
 * replaced, which hashed *and* compared structurally, and so silently collapsed exactly the keys an
 * identity map is meant to keep apart.
 */
internal class IdentityKeyedMap<K, V> : AbstractMutableMap<K, V>() {

    private class Ref<K>(val key: K) {
        override fun hashCode(): Int = key?.hashCode() ?: 0

        override fun equals(other: Any?): Boolean =
            this === other || (other is Ref<*> && other.key === key)
    }

    private val backing = mutableMapOf<Ref<K>, V>()

    override val size: Int
        get() = backing.size

    override fun put(key: K, value: V): V? = backing.put(Ref(key), value)

    override fun get(key: K): V? = backing[Ref(key)]

    override fun containsKey(key: K): Boolean = backing.containsKey(Ref(key))

    override fun remove(key: K): V? = backing.remove(Ref(key))

    override fun clear() = backing.clear()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() =
            object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
                override val size: Int
                    get() = backing.size

                // A view, not a builder: entries are added through the map itself.
                override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                    throw UnsupportedOperationException(
                        "Entries are added through the map, not through its entry set"
                    )

                override fun clear() = backing.clear()

                override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                    val delegate = backing.entries.iterator()
                    return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                        override fun hasNext(): Boolean = delegate.hasNext()

                        override fun next(): MutableMap.MutableEntry<K, V> {
                            val entry = delegate.next()
                            return object : MutableMap.MutableEntry<K, V> {
                                override val key: K = entry.key.key

                                override val value: V = entry.value

                                override fun setValue(newValue: V): V = entry.setValue(newValue)
                            }
                        }

                        // Removal has to reach the backing map so the view stays a view.
                        override fun remove() = delegate.remove()
                    }
                }
            }
}
