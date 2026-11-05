package org.cqframework.cql.ucum

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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

    @Test
    fun testIsArbitraryDetectsArbitraryUnits() {
        val ucumService = DefaultUcumService()
        assertTrue(ucumService.isArbitrary("[IU]"))
        // Case-sensitive: [iU] and [IU] are both distinct, arbitrary units.
        assertTrue(ucumService.isArbitrary("[iU]"))
        assertTrue(ucumService.isArbitrary("[PFU]"))
        assertTrue(ucumService.isArbitrary("[arb'U]"))
        // Expressions involving an arbitrary atom, including prefixes, exponents, and denominators.
        assertTrue(ucumService.isArbitrary("[IU]/mL"))
        assertTrue(ucumService.isArbitrary("mL/[IU]"))
        assertTrue(ucumService.isArbitrary("k[IU]"))
        assertTrue(ucumService.isArbitrary("[IU]2"))
    }

    @Test
    fun testIsArbitraryRejectsNonArbitraryUnits() {
        val ucumService = DefaultUcumService()
        assertFalse(ucumService.isArbitrary("mg"))
        assertFalse(ucumService.isArbitrary("mg/dL"))
        assertFalse(ucumService.isArbitrary("1"))
        assertFalse(ucumService.isArbitrary(""))
        // Bracketed but not arbitrary (inch, degree Fahrenheit).
        assertFalse(ucumService.isArbitrary("[in_i]"))
        assertFalse(ucumService.isArbitrary("[degF]"))
        // An arbitrary code appearing only inside an annotation must not match.
        assertFalse(ucumService.isArbitrary("mg{[IU] per dose}"))
    }
}
