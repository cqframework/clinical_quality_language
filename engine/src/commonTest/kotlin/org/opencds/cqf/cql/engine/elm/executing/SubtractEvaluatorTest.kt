package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class SubtractEvaluatorTest {
    private fun uncertainty(low: Int, high: Int) =
        Interval(low.toCqlInteger(), true, high.toCqlInteger(), true, true)

    /**
     * [#1938](https://github.com/cqframework/clinical_quality_language/issues/1938): a value in
     * [a, b] minus a value in [c, d] can be anywhere in [a - d, b - c]
     */
    @Test
    fun subtractUncertainties() {
        val result =
            assertIs<Interval>(
                SubtractEvaluator.subtract(uncertainty(16, 44), uncertainty(4, 16), null)
            )
        assertEquals(0.toCqlInteger(), result.start)
        assertEquals(40.toCqlInteger(), result.end)

        val reversed =
            assertIs<Interval>(
                SubtractEvaluator.subtract(uncertainty(4, 16), uncertainty(16, 44), null)
            )
        assertEquals((-40).toCqlInteger(), reversed.start)
        assertEquals(0.toCqlInteger(), reversed.end)
    }
}
