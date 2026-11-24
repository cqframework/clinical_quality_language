package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

/**
 * Covers the path-parse memoization in [PropertyEvaluator.resolvePath]. The cache keys on the path
 * string and is process-global, so these tests also assert the key property that makes it safe:
 * resolution is correct regardless of cache state (hit, miss, or saturated), which is why they pass
 * in any order even though they share the singleton cache.
 */
class PropertyEvaluatorTest {

    @Test
    fun resolvesSimpleQualifiedAndIndexedPaths() {
        val target =
            Tuple(
                mapOf(
                    "x" to 5.toCqlInteger(),
                    "a" to Tuple(mapOf("b" to 7.toCqlInteger())),
                    "items" to
                        listOf(10.toCqlInteger(), 20.toCqlInteger(), 30.toCqlInteger()).toCqlList(),
                )
            )

        assertEquals(5.toCqlInteger(), PropertyEvaluator.resolvePath(target, "x"))
        assertEquals(7.toCqlInteger(), PropertyEvaluator.resolvePath(target, "a.b"))
        assertEquals(20.toCqlInteger(), PropertyEvaluator.resolvePath(target, "items[1]"))
    }

    @Test
    fun repeatedResolutionOfSamePathIsStable() {
        // Second call is a cache hit; must equal the first (miss).
        val target = Tuple(mapOf("a" to Tuple(mapOf("b" to 7.toCqlInteger()))))
        val first = PropertyEvaluator.resolvePath(target, "a.b")
        val second = PropertyEvaluator.resolvePath(target, "a.b")
        assertEquals(first, second)
        assertEquals(7.toCqlInteger(), second)
    }

    @Test
    fun distinctRuntimePathsEachResolveCorrectly() {
        // Simulates ForEach feeding distinct, runtime-computed path strings: each must resolve on
        // its own key, with no cross-contamination between cached entries, on both miss and hit.
        val n = 200
        val target = Tuple((0 until n).associate { "k$it" to it.toCqlInteger() })
        repeat(2) {
            for (i in 0 until n) {
                assertEquals(i.toCqlInteger(), PropertyEvaluator.resolvePath(target, "k$i"))
            }
        }
    }

    @Test
    fun parsingStillWorksPastTheCacheCap() {
        // Fill the path cache past MAX_CACHED_PATHS with distinct unknown paths, then confirm the
        // parser keeps running (returning null for an unknown key, never throwing) and that a known
        // path still resolves correctly once the cache is saturated and no longer accepting
        // entries.
        val target = Tuple(mapOf("known" to 42.toCqlInteger()))
        for (i in 0..10_050) {
            assertNull(PropertyEvaluator.resolvePath(target, "missing_$i"))
        }
        assertEquals(42.toCqlInteger(), PropertyEvaluator.resolvePath(target, "known"))
    }
}
