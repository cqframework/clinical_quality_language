package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class CeilingEvaluatorTest {
    private fun ceilingOf(value: String) =
        CeilingEvaluator.ceiling(BigDecimal(value).toCqlDecimal())

    @Test
    fun ceiling() {
        assertNull(CeilingEvaluator.ceiling(null))
        assertEquals(1.toCqlInteger(), ceilingOf("1.0"))
        assertEquals(2.toCqlInteger(), ceilingOf("1.1"))
        assertEquals(0.toCqlInteger(), ceilingOf("-0.1"))
        assertEquals((-1).toCqlInteger(), ceilingOf("-1.0"))
        assertEquals((-1).toCqlInteger(), ceilingOf("-1.1"))
    }

    /**
     * [#1849](https://github.com/cqframework/clinical_quality_language/issues/1849): Return null
     * instead of wrapping around when the result is outside the Integer range
     */
    @Test
    fun ceilingAtIntegerRangeLimits() {
        assertEquals(Int.MAX_VALUE.toCqlInteger(), ceilingOf("2147483647.0"))
        assertEquals(Int.MAX_VALUE.toCqlInteger(), ceilingOf("2147483646.2"))
        assertNull(ceilingOf("2147483647.2"))
        assertNull(ceilingOf("2147483648.2"))
        assertNull(ceilingOf("99999999999999999999.99999999"))

        assertEquals(Int.MIN_VALUE.toCqlInteger(), ceilingOf("-2147483648.0"))
        assertEquals(Int.MIN_VALUE.toCqlInteger(), ceilingOf("-2147483648.2"))
        assertNull(ceilingOf("-2147483649.2"))
        assertNull(ceilingOf("-99999999999999999999.99999999"))
    }

    /**
     * [#1849](https://github.com/cqframework/clinical_quality_language/issues/1849): Do not lose a
     * small fractional part of a large value
     */
    @Test
    fun ceilingOfLargeValueWithSmallFraction() {
        assertEquals(1000000001.toCqlInteger(), ceilingOf("1000000000.00000001"))
        assertEquals(Int.MAX_VALUE.toCqlInteger(), ceilingOf("2147483646.00000001"))
        assertEquals((-1000000000).toCqlInteger(), ceilingOf("-1000000000.00000001"))
    }

    @Test
    fun ceilingOfQuantity() {
        assertEquals(
            2.toCqlInteger(),
            CeilingEvaluator.ceiling(Quantity().withValue(BigDecimal("1.1")).withUnit("mg")),
        )
        assertNull(
            CeilingEvaluator.ceiling(
                Quantity().withValue(BigDecimal("2147483647.2")).withUnit("mg")
            )
        )
    }
}
