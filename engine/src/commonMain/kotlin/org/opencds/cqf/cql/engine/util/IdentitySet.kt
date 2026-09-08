package org.opencds.cqf.cql.engine.util

/**
 * A [MutableSet] that distinguishes elements by reference identity rather than by [equals] and
 * [hashCode].
 *
 * Backed by [createIdentityHashMap], which supplies a true identity map on the JVM.
 */
class IdentitySet<E> : AbstractMutableSet<E>() {

    private val backing: MutableMap<E, Boolean> = createIdentityHashMap()

    override val size: Int
        get() = backing.size

    override fun add(element: E): Boolean = backing.put(element, true) == null

    override fun contains(element: E): Boolean = backing.containsKey(element)

    override fun remove(element: E): Boolean = backing.remove(element) != null

    override fun clear() = backing.clear()

    override fun iterator(): MutableIterator<E> = backing.keys.iterator()
}
