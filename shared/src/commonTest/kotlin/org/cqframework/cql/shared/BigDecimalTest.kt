package org.cqframework.cql.shared

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BigDecimalTest {
    @Test
    fun scale() {
        fun assertScale(expected: Int, value: String) {
            val actual = BigDecimal(value).scale()
            assertEquals(expected, actual)
        }

        assertScale(0, "123")
        assertScale(0, "0")
        assertScale(2, "1.23")
        assertScale(3, "1.200")

        assertScale(3, "123e-3")
        assertScale(4, "12.3e-3")
    }

    @Test
    fun arithmeticKeepsExactValues() {
        assertEquals("0.0006", BigDecimal("0.02").multiply(BigDecimal("0.03")).toPlainString())
        assertEquals("2.25", BigDecimal("1.5").multiply(BigDecimal("1.5")).toPlainString())
        assertEquals("0.125", BigDecimal("1.0").divide(BigDecimal("8.0")).toPlainString())
        assertEquals("2.0", BigDecimal("1.00").divide(BigDecimal("0.5")).toPlainString())
        assertFailsWith<ArithmeticException> { BigDecimal("1.0").divide(BigDecimal("3.0")) }
        assertEquals(
            "0.33333333",
            BigDecimal("1.0").divide(BigDecimal("3.0"), 8, RoundingMode.FLOOR).toPlainString(),
        )
        assertEquals("1.0", BigDecimal("10.0").remainder(BigDecimal("3.0")).toPlainString())
        val quotientAndRemainder = BigDecimal("10.1").divideAndRemainder(BigDecimal("-3.1"))
        assertEquals("-3", quotientAndRemainder[0].toPlainString())
        assertEquals("0.8", quotientAndRemainder[1].toPlainString())
    }

    @Test
    fun stripTrailingZeros() {
        assertEquals("1", BigDecimal("1.00").stripTrailingZeros().toPlainString())
        assertEquals("1E+2", BigDecimal("100").stripTrailingZeros().toString())
        assertEquals(0, BigDecimal("0.000").stripTrailingZeros().scale())
    }

    @Test
    fun setScale() {
        assertEquals("1.24", BigDecimal("1.235").setScale(2, RoundingMode.HALF_UP).toPlainString())
        assertEquals(
            "1.23",
            BigDecimal("1.235").setScale(2, RoundingMode.HALF_DOWN).toPlainString(),
        )
        assertEquals("-1.24", BigDecimal("-1.231").setScale(2, RoundingMode.FLOOR).toPlainString())
        assertFailsWith<ArithmeticException> { BigDecimal("1.235").setScale(2) }
    }
}
