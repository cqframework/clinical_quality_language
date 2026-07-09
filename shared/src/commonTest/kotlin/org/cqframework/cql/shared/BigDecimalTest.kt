package org.cqframework.cql.shared

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
