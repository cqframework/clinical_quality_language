package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals

class FlattenEvaluatorTest {
    /**
     * [#1748](https://github.com/cqframework/clinical_quality_language/issues/1748): Treat nulls as
     * empty lists in the list passed to `flatten()`
     */
    @Test
    fun flattenTest() {
        val list = listOf(listOf(1, 2, 3), null, listOf(4, 5))
        val expected = listOf(1, 2, 3, 4, 5)

        assertEquals(expected, FlattenEvaluator.flatten(list))
    }
}
