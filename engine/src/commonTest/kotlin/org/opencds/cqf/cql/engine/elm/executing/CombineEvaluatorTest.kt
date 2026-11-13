package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class CombineEvaluatorTest {
    private fun listOf(vararg values: kotlin.String?) = values.map { it?.toCqlString() }.toCqlList()

    @Test
    fun combine() {
        assertNull(CombineEvaluator.combine(null, "-".toCqlString()))
        assertNull(CombineEvaluator.combine(listOf(), "-".toCqlString()))
        assertEquals(
            "abc".toCqlString(),
            CombineEvaluator.combine(listOf("a", "b", "c"), "".toCqlString()),
        )
        assertEquals(
            "a-b-c".toCqlString(),
            CombineEvaluator.combine(listOf("a", "b", "c"), "-".toCqlString()),
        )
    }

    /**
     * [#1916](https://github.com/cqframework/clinical_quality_language/issues/1916): A null
     * separator and null elements are ignored
     */
    @Test
    fun combineIgnoresNulls() {
        assertEquals("abc".toCqlString(), CombineEvaluator.combine(listOf("a", "b", "c"), null))
        assertEquals(
            "abc".toCqlString(),
            CombineEvaluator.combine(listOf("a", "b", "c", null), "".toCqlString()),
        )
        assertEquals(
            "a-c".toCqlString(),
            CombineEvaluator.combine(listOf("a", null, "c"), "-".toCqlString()),
        )
        assertEquals(
            "ac".toCqlString(),
            CombineEvaluator.combine(listOf(null, "a", null, "c", null), null),
        )
        assertNull(CombineEvaluator.combine(listOf(null, null), "-".toCqlString()))
    }
}
