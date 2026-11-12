package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

class SliceEvaluatorTest {
    private fun listOf(vararg values: Int) = values.map { it.toCqlInteger() }.toCqlList()

    private fun slice(start: Int?, end: Int?) =
        SliceEvaluator.slice(listOf(1, 2, 3), start?.toCqlInteger(), end?.toCqlInteger())

    @Test
    fun slice() {
        assertNull(SliceEvaluator.slice(null, 1.toCqlInteger(), null))
        assertEquals(listOf(2, 3), slice(1, null))
        assertEquals(listOf(1, 2), slice(0, 2))
        assertEquals(listOf(), slice(3, null))
        assertEquals(listOf(), slice(5, null))
        assertEquals(listOf(), slice(2, 1))
    }

    /**
     * [#1915](https://github.com/cqframework/clinical_quality_language/issues/1915): A null start
     * index begins at the first element, and a negative start or end index gives an empty list
     */
    @Test
    fun nullOrNegativeIndex() {
        // Skip(X, null) is Slice(X, null, null)
        assertEquals(listOf(1, 2, 3), slice(null, null))
        assertEquals(listOf(1, 2), slice(null, 2))
        // Skip(X, -1) is Slice(X, -1, null)
        assertEquals(listOf(), slice(-1, null))
        assertEquals(listOf(), slice(-5, null))
        // Take(X, -1) is Slice(X, 0, -1)
        assertEquals(listOf(), slice(0, -1))
        assertEquals(listOf(), slice(null, -1))
    }
}
