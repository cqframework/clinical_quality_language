package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class SplitEvaluatorTest {
    @Test
    fun testSplit() {
        assertEquals(null, SplitEvaluator.split(null, "abc".toCqlString()))
        assertEquals(
            mutableListOf("test".toCqlString()).toCqlList(),
            SplitEvaluator.split("test".toCqlString(), null),
        )
        assertEquals(
            mutableListOf(
                    "t".toCqlString(),
                    "e".toCqlString(),
                    "s".toCqlString(),
                    "t".toCqlString(),
                )
                .toCqlList(),
            SplitEvaluator.split("test".toCqlString(), String.EMPTY_STRING),
        )
        assertEquals(
            mutableListOf(String.EMPTY_STRING).toCqlList(),
            SplitEvaluator.split(String.EMPTY_STRING, ",".toCqlString()),
        )
        assertEquals(
            mutableListOf(String.EMPTY_STRING).toCqlList(),
            SplitEvaluator.split(String.EMPTY_STRING, String.EMPTY_STRING),
        )
        assertEquals(
            mutableListOf("es".toCqlString()).toCqlList(),
            SplitEvaluator.split("test".toCqlString(), "t".toCqlString()),
        )
        assertEquals(
            mutableListOf("t".toCqlString(), "st".toCqlString()).toCqlList(),
            SplitEvaluator.split("teeest".toCqlString(), "e".toCqlString()),
        )
        assertEquals(
            mutableListOf("test".toCqlString()).toCqlList(),
            SplitEvaluator.split("test".toCqlString(), "abc".toCqlString()),
        )
    }
}
