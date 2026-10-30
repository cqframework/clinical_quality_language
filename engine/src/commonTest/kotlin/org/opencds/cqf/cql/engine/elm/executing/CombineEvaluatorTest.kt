package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class CombineEvaluatorTest {
    private val abc = listOf("a".toCqlString(), "b".toCqlString(), "c".toCqlString()).toCqlList()

    @Test
    fun combine() {
        assertEquals("abc".toCqlString(), CombineEvaluator.combine(abc, "".toCqlString()))
        assertEquals("a-b-c".toCqlString(), CombineEvaluator.combine(abc, "-".toCqlString()))
    }
}
