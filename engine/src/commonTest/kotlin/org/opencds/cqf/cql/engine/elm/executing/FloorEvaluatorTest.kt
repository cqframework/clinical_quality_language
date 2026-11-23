package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class FloorEvaluatorTest {
    private fun floorOf(value: String) = FloorEvaluator.floor(BigDecimal(value).toCqlDecimal())

    @Test
    fun floor() {
        assertNull(FloorEvaluator.floor(null))
        assertEquals(1.toCqlInteger(), floorOf("1.0"))
        assertEquals(1.toCqlInteger(), floorOf("1.1"))
        assertEquals((-1).toCqlInteger(), floorOf("-0.1"))
        assertEquals((-1).toCqlInteger(), floorOf("-1.0"))
        assertEquals((-2).toCqlInteger(), floorOf("-1.1"))
    }

    /**
     * [#1849](https://github.com/cqframework/clinical_quality_language/issues/1849): Return null
     * instead of wrapping around when the result is outside the Integer range
     */
    @Test
    fun floorAtIntegerRangeLimits() {
        assertEquals(Int.MAX_VALUE.toCqlInteger(), floorOf("2147483647.0"))
        assertEquals(Int.MAX_VALUE.toCqlInteger(), floorOf("2147483647.2"))
        assertNull(floorOf("2147483648.2"))
        assertNull(floorOf("99999999999999999999.99999999"))

        assertEquals(Int.MIN_VALUE.toCqlInteger(), floorOf("-2147483648.0"))
        assertEquals(Int.MIN_VALUE.toCqlInteger(), floorOf("-2147483647.2"))
        assertNull(floorOf("-2147483648.2"))
        assertNull(floorOf("-2147483649.2"))
        assertNull(floorOf("-99999999999999999999.99999999"))
    }

    /**
     * [#1849](https://github.com/cqframework/clinical_quality_language/issues/1849): Do not lose a
     * small fractional part of a large value
     */
    @Test
    fun floorOfLargeValueWithSmallFraction() {
        assertEquals((-1000000001).toCqlInteger(), floorOf("-1000000000.00000001"))
        assertEquals(Int.MIN_VALUE.toCqlInteger(), floorOf("-2147483647.00000001"))
        assertEquals(1000000000.toCqlInteger(), floorOf("1000000000.00000001"))
    }

    @Test
    fun floorOfQuantity() {
        assertEquals(
            1.toCqlInteger(),
            FloorEvaluator.floor(Quantity().withValue(BigDecimal("1.1")).withUnit("mg")),
        )
        assertNull(
            FloorEvaluator.floor(Quantity().withValue(BigDecimal("-2147483648.2")).withUnit("mg"))
        )
    }
}
