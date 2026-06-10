package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList

class FlattenEvaluatorTest {
    /**
     * [#1748](https://github.com/cqframework/clinical_quality_language/issues/1748): Treat nulls as
     * empty lists in the list passed to `flatten()`
     */
    @Test
    fun flattenTest() {
        val list =
            listOf(
                    listOf(1.toCqlInteger(), 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(),
                    null,
                    listOf(4.toCqlInteger(), 5.toCqlInteger()).toCqlList(),
                )
                .toCqlList()
        val expected =
            listOf(
                    1.toCqlInteger(),
                    2.toCqlInteger(),
                    3.toCqlInteger(),
                    4.toCqlInteger(),
                    5.toCqlInteger(),
                )
                .toCqlList()

        assertEquals(expected, FlattenEvaluator.flatten(list))
    }
}
