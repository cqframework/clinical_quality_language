package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlString

class LastPositionOfEvaluatorTest {
    private fun lastPositionOf(pattern: String, string: String) =
        LastPositionOfEvaluator.lastPositionOf(string.toCqlString(), pattern.toCqlString())

    @Test
    fun lastPositionOf() {
        assertNull(LastPositionOfEvaluator.lastPositionOf(null, "B".toCqlString()))
        assertNull(LastPositionOfEvaluator.lastPositionOf("ABCDE".toCqlString(), null))
        assertEquals(7.toCqlInteger(), lastPositionOf("B", "ABCDEDCBA"))
        assertEquals(4.toCqlInteger(), lastPositionOf("E", "ABCDE"))
        assertEquals(3.toCqlInteger(), lastPositionOf("DE", "ABCDE"))
        assertEquals((-1).toCqlInteger(), lastPositionOf("XYZ", "ABCDE"))
    }

    /**
     * [#1917](https://github.com/cqframework/clinical_quality_language/issues/1917): The empty
     * pattern's last match is after the final character, so the result is the argument's length
     */
    @Test
    fun lastPositionOfEmptyPattern() {
        assertEquals(5.toCqlInteger(), lastPositionOf("", "ABCDE"))
        assertEquals(15.toCqlInteger(), lastPositionOf("", "Say hi to Ohio!"))
        assertEquals(0.toCqlInteger(), lastPositionOf("", ""))
    }
}
