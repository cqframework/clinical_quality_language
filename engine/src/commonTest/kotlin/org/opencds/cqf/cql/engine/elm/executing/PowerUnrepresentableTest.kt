package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlLong

/**
 * "If the result of the operation cannot be represented, the result is null."
 * (https://cql.hl7.org/09-b-cqlreference.html#power), as Exp, Ln and Log already do.
 */
class PowerUnrepresentableTest {
    private fun decimal(value: String) = BigDecimal(value).toCqlDecimal()

    @Test
    fun zeroToANegativeExponentIsNull() {
        assertNull(PowerEvaluator.power(0.toCqlInteger(), (-1).toCqlInteger()))
        assertNull(PowerEvaluator.power(0L.toCqlLong(), (-1L).toCqlLong()))
        assertNull(PowerEvaluator.power(decimal("0.0"), decimal("-1.0")))
    }

    @Test
    fun undefinedOrInfiniteDecimalResultsAreNull() {
        assertNull(PowerEvaluator.power(decimal("-1.0"), decimal("0.5")))
        assertNull(PowerEvaluator.power(decimal("10.0"), decimal("1000.0")))
    }

    @Test
    fun geometricMeanOfANegativeProductIsNull() {
        assertNull(
            GeometricMeanEvaluator.geometricMean(
                listOf(decimal("-2.0"), decimal("8.0")).toCqlList(),
                null,
            )
        )
    }

    @Test
    fun representableResultsAreUnchanged() {
        assertEquals(decimal("0.25"), PowerEvaluator.power(2.toCqlInteger(), (-2).toCqlInteger()))
        assertEquals(4.toCqlInteger(), PowerEvaluator.power(2.toCqlInteger(), 2.toCqlInteger()))
        assertEquals(1.toCqlInteger(), PowerEvaluator.power(0.toCqlInteger(), 0.toCqlInteger()))
        assertEquals(decimal("0.25"), PowerEvaluator.power(decimal("2.0"), decimal("-2.0")))
    }
}
