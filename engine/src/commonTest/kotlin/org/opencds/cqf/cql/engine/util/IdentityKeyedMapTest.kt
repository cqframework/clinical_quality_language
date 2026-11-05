package org.opencds.cqf.cql.engine.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * [IdentityKeyedMap] backs `createIdentityHashMap` on JS and wasm, where the JVM's
 * `java.util.IdentityHashMap` is unavailable. It is exercised there through [IdentitySet], but the
 * JVM — which is where coverage is measured — never reaches it, so it is tested directly.
 */
internal class IdentityKeyedMapTest {

    /**
     * Structural equality, so an equality-keyed map would collapse two of these and this one must
     * not.
     */
    private data class Key(val id: String)

    @Test
    fun equalButDistinctKeysAreSeparateEntries() {
        val first = Key("a")
        val second = Key("a")

        assertEquals(first, second, "fixture should be structurally equal")
        assertFalse(first === second, "fixture should be two distinct instances")

        val map = IdentityKeyedMap<Key, String>()
        map[first] = "first"
        map[second] = "second"

        assertEquals(2, map.size)
        assertEquals("first", map[first])
        assertEquals("second", map[second])
    }

    @Test
    fun putReplacesOnlyTheSameReference() {
        val key = Key("a")
        val equalKey = Key("a")

        val map = IdentityKeyedMap<Key, String>()
        assertNull(map.put(key, "first"), "no previous value for a new key")
        assertEquals("first", map.put(key, "second"), "put returns the value it replaced")
        assertEquals(1, map.size)

        assertNull(map.put(equalKey, "third"), "an equal key is a different entry")
        assertEquals(2, map.size)
    }

    @Test
    fun getAndContainsKeyAnswerByReference() {
        val present = Key("a")
        val equalButAbsent = Key("a")

        val map = IdentityKeyedMap<Key, String>()
        map[present] = "value"

        assertTrue(map.containsKey(present))
        assertEquals("value", map[present])

        assertFalse(map.containsKey(equalButAbsent))
        assertNull(map[equalButAbsent])
    }

    @Test
    fun removeAnswersByReference() {
        val present = Key("a")
        val equalButAbsent = Key("a")

        val map = IdentityKeyedMap<Key, String>()
        map[present] = "value"

        assertNull(map.remove(equalButAbsent), "an equal key should not remove the stored entry")
        assertEquals(1, map.size)

        assertEquals("value", map.remove(present))
        assertEquals(0, map.size)
    }

    @Test
    fun clearEmptiesTheMap() {
        val map = IdentityKeyedMap<Key, String>()
        map[Key("a")] = "1"
        map[Key("b")] = "2"

        map.clear()

        assertEquals(0, map.size)
        assertTrue(map.isEmpty())
    }

    @Test
    fun holdsANullKey() {
        // Ref hashes a null key to 0 rather than dereferencing it.
        val map = IdentityKeyedMap<Key?, String>()
        map[null] = "value"

        assertEquals(1, map.size)
        assertTrue(map.containsKey(null))
        assertEquals("value", map[null])
        assertEquals("value", map.remove(null))
        assertEquals(0, map.size)
    }

    @Test
    fun entriesExposeTheStoredKeysAndValues() {
        val first = Key("a")
        val second = Key("a")

        val map = IdentityKeyedMap<Key, String>()
        map[first] = "first"
        map[second] = "second"

        val entries = map.entries.toList()
        assertEquals(2, map.entries.size)
        assertEquals(2, entries.size)

        // Unwrapped back to the original key references, not the internal Ref wrapper.
        assertTrue(entries.any { it.key === first && it.value == "first" })
        assertTrue(entries.any { it.key === second && it.value == "second" })
    }

    @Test
    fun entrySetValueWritesThroughToTheMap() {
        val key = Key("a")
        val map = IdentityKeyedMap<Key, String>()
        map[key] = "before"

        val entry = map.entries.first()
        assertEquals("before", entry.setValue("after"), "setValue returns the previous value")

        assertEquals("after", map[key])
    }

    @Test
    fun entryIteratorRemovalWritesThroughToTheMap() {
        val first = Key("a")
        val second = Key("b")

        val map = IdentityKeyedMap<Key, String>()
        map[first] = "1"
        map[second] = "2"

        val iterator = map.entries.iterator()
        assertTrue(iterator.hasNext())
        val removed = iterator.next().key
        iterator.remove()

        assertEquals(1, map.size)
        assertFalse(map.containsKey(removed))
    }

    @Test
    fun entriesCannotBeAddedThroughTheView() {
        val map = IdentityKeyedMap<Key, String>()
        map[Key("a")] = "1"
        val existing = map.entries.first()

        assertFailsWith<UnsupportedOperationException> { map.entries.add(existing) }
    }

    @Test
    fun clearingTheEntryViewClearsTheMap() {
        val map = IdentityKeyedMap<Key, String>()
        map[Key("a")] = "1"
        map[Key("b")] = "2"

        map.entries.clear()

        assertEquals(0, map.size)
    }

    @Test
    fun derivedKeyAndValueViewsReflectTheEntries() {
        val first = Key("a")
        val second = Key("a")

        val map = IdentityKeyedMap<Key, String>()
        map[first] = "first"
        map[second] = "second"

        val keys = map.keys.toList()
        assertEquals(2, keys.size)
        assertTrue(keys.any { it === first })
        assertTrue(keys.any { it === second })

        assertEquals(listOf("first", "second").sorted(), map.values.toList().sorted())
    }

    @Test
    fun getOrPutStoresPerReference() {
        // The shape Profile.ensureChild relies on: an absent key is populated, a present one
        // reused.
        val first = Key("a")
        val second = Key("a")

        val map = IdentityKeyedMap<Key, MutableList<String>>()
        val createdForFirst = map.getOrPut(first) { mutableListOf() }
        val reusedForFirst = map.getOrPut(first) { mutableListOf() }
        val createdForSecond = map.getOrPut(second) { mutableListOf() }

        assertSame(createdForFirst, reusedForFirst, "the same reference reuses its entry")
        assertFalse(createdForFirst === createdForSecond, "an equal key gets its own entry")
        assertEquals(2, map.size)
    }
}
