package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals

class SplitEvaluatorTest {
    @Test
    fun testSplit() {
        assertEquals(null, SplitEvaluator.split(null, "abc"))
        assertEquals(mutableListOf("test"), SplitEvaluator.split("test", null))
        assertEquals(mutableListOf("test"), SplitEvaluator.split("test", ""))
        assertEquals(mutableListOf("es"), SplitEvaluator.split("test", "t"))
        assertEquals(mutableListOf("t", "st"), SplitEvaluator.split("teeest", "e"))
        assertEquals(mutableListOf("test"), SplitEvaluator.split("test", "abc"))
    }
}
