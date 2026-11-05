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

    /**
     * [#1870](https://github.com/cqframework/clinical_quality_language/issues/1870): Split with a
     * separator works on JS and Wasm, not just on the JVM
     */
    @Test
    fun testSplitWithSeparator() {
        assertEquals(
            mutableListOf("Patient".toCqlString(), "123".toCqlString()).toCqlList(),
            SplitEvaluator.split("Patient/123".toCqlString(), "/".toCqlString()),
        )
        assertEquals(
            mutableListOf("a".toCqlString(), "b".toCqlString()).toCqlList(),
            SplitEvaluator.split(",a,,b,".toCqlString(), ",".toCqlString()),
        )
    }
}
