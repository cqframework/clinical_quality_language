package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal

/**
 * Decimal equivalence compares at the precision of the least precise operand, ignoring trailing
 * zeroes after the decimal point (https://cql.hl7.org/09-b-cqlreference.html#equivalent). Digits
 * before the decimal point do not change that precision, so 10.000 has the same precision as 1.000.
 */
class DecimalEquivalentTest {
    private fun equivalent(left: String, right: String) =
        EquivalentEvaluator.equivalent(
            BigDecimal(left).toCqlDecimal(),
            BigDecimal(right).toCqlDecimal(),
        )

    @Test
    fun trailingZeroesAreIgnoredWhateverTheIntegerPart() {
        // cql-tests EquivFloatTrailingZero
        assertEquals(Boolean.TRUE, equivalent("1.001", "1.000"))
        assertEquals(Boolean.TRUE, equivalent("10.001", "10.000"))
        assertEquals(Boolean.TRUE, equivalent("10.000", "10.001"))
        assertEquals(Boolean.TRUE, equivalent("100.00", "100.3"))
        assertEquals(Boolean.TRUE, equivalent("1500.0", "1500.2"))
        assertEquals(Boolean.TRUE, equivalent("-20.0", "-20.4"))
    }

    @Test
    fun valuesThatDifferAtTheCommonPrecisionAreNotEquivalent() {
        assertEquals(Boolean.FALSE, equivalent("10.0", "10.6"))
        assertEquals(Boolean.FALSE, equivalent("10.0", "11.0"))
        assertEquals(Boolean.FALSE, equivalent("10.0", "20.0"))
        assertEquals(Boolean.FALSE, equivalent("100.00", "101"))
        assertEquals(Boolean.FALSE, equivalent("1.50", "1.55"))
        assertEquals(Boolean.TRUE, equivalent("10.0", "10.0"))
        assertEquals(Boolean.TRUE, equivalent("10", "10.00"))
    }

    @Test
    fun quantityValuesFollowTheSameRule() {
        assertEquals(
            Boolean.TRUE,
            EquivalentEvaluator.equivalent(
                Quantity().withValue(BigDecimal("10.0")).withUnit("mg"),
                Quantity().withValue(BigDecimal("10.04")).withUnit("mg"),
            ),
        )
    }
}
