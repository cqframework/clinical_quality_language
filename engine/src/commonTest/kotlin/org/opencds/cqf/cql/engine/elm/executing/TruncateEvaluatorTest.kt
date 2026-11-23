package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class TruncateEvaluatorTest {
    private fun truncateOf(value: String) =
        TruncateEvaluator.truncate(BigDecimal(value).toCqlDecimal())

    @Test
    fun truncate() {
        assertNull(TruncateEvaluator.truncate(null))
        assertEquals(0.toCqlInteger(), truncateOf("0.1"))
        assertEquals(1.toCqlInteger(), truncateOf("1.0"))
        assertEquals(1.toCqlInteger(), truncateOf("1.9"))
        assertEquals(0.toCqlInteger(), truncateOf("-0.1"))
        assertEquals((-1).toCqlInteger(), truncateOf("-1.0"))
        assertEquals((-1).toCqlInteger(), truncateOf("-1.9"))
    }

    /**
     * [#1849](https://github.com/cqframework/clinical_quality_language/issues/1849): Return null
     * instead of wrapping around when the result is outside the Integer range
     */
    @Test
    fun truncateAtIntegerRangeLimits() {
        assertEquals(Int.MAX_VALUE.toCqlInteger(), truncateOf("2147483647.0"))
        assertEquals(Int.MAX_VALUE.toCqlInteger(), truncateOf("2147483647.2"))
        assertNull(truncateOf("2147483648.2"))
        assertNull(truncateOf("99999999999999999999.99999999"))

        assertEquals(Int.MIN_VALUE.toCqlInteger(), truncateOf("-2147483648.0"))
        assertEquals(Int.MIN_VALUE.toCqlInteger(), truncateOf("-2147483648.2"))
        assertNull(truncateOf("-2147483649.2"))
        assertNull(truncateOf("-99999999999999999999.99999999"))
    }
}
