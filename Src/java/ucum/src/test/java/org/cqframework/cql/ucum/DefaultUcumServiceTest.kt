package org.cqframework.cql.ucum

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DefaultUcumServiceTest {

    @Test
    fun testConvert() {
        val ucumService = DefaultUcumService()
        val result = ucumService.convert(BigDecimal("1"), "mg", "g")
        assertEquals(BigDecimal("0.0010"), result)
    }

    @Test
    fun testValidate() {
        val ucumService = DefaultUcumService()
        assertNull(ucumService.validate("mg"))
        assertTrue(ucumService.validate("foo")?.contains("The unit 'foo' is unknown") ?: false)
    }
}
