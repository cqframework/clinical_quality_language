package org.opencds.cqf.cql.engine.util

import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.State

internal class UnitUtilsTest : CqlTestBase() {
    private val state: State by lazy { State(Environment(libraryManager)) }

    private fun combine(left: String, right: String, op: UnitOperation): String? =
        UnitUtils.combineUnits(left, right, op, state)

    private fun convert(value: String, from: String, to: String): BigDecimal? =
        UnitUtils.convertUnit(BigDecimal(value), from, to, state)

    // ---------- combineUnits: multiplication ----------

    @Test
    fun multiplyDistinctBaseUnitsJoinsFactors() {
        assertEquals("m.s", combine("m", "s", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplySameBaseUnitAddsExponents() {
        assertEquals("m2", combine("m", "m", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyExistingExponentIsAccumulated() {
        assertEquals("m3", combine("m2", "m", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyByUnityLeavesOtherUnitUnchanged() {
        assertEquals("m", combine("1", "m", UnitOperation.MULTIPLY))
        assertEquals("m", combine("m", "1", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyQuotientByBaseUnitCancelsDenominator() {
        // (m/s) * s == m
        assertEquals("m", combine("m/s", "s", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyBaseUnitByQuotientCancelsDenominator() {
        // m * (s/m) == s
        assertEquals("s", combine("m", "s/m", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyQuotientByUnityIsUnchanged() {
        assertEquals("m/s", combine("m/s", "1", UnitOperation.MULTIPLY))
    }

    @Test
    fun multiplyInvalidUnitReturnsNull() {
        assertNull(combine("xyz", "m", UnitOperation.MULTIPLY))
        assertNull(combine("m", "xyz", UnitOperation.MULTIPLY))
    }

    // ---------- combineUnits: division ----------

    @Test
    fun divideDistinctBaseUnitsProducesQuotient() {
        assertEquals("m/s", combine("m", "s", UnitOperation.DIVIDE))
    }

    @Test
    fun divideSameUnitProducesUnity() {
        assertEquals("1", combine("m", "m", UnitOperation.DIVIDE))
    }

    @Test
    fun divideSameQuotientProducesUnity() {
        assertEquals("1", combine("m/s", "m/s", UnitOperation.DIVIDE))
    }

    @Test
    fun divideByUnityLeavesUnitUnchanged() {
        assertEquals("m", combine("m", "1", UnitOperation.DIVIDE))
    }

    @Test
    fun divideSquaredUnitReducesExponent() {
        // m2 / m == m
        assertEquals("m", combine("m2", "m", UnitOperation.DIVIDE))
    }

    @Test
    fun divideLeftUnityProducesReciprocal() {
        assertEquals("/s", combine("1", "s", UnitOperation.DIVIDE))
    }

    @Test
    fun divideInvalidUnitReturnsNull() {
        assertNull(combine("m", "xyz", UnitOperation.DIVIDE))
    }

    // ---------- combineUnits: null/blank handling ----------

    @Test
    fun blankUnitIsTreatedAsUnity() {
        assertEquals("m", combine("", "m", UnitOperation.MULTIPLY))
        assertEquals("m", combine("m", "", UnitOperation.DIVIDE))
    }

    // ---------- convertUnit ----------

    @Test
    fun convertIdenticalUnitsReturnsValueUnchanged() {
        val value = BigDecimal("5")
        assertSame(value, UnitUtils.convertUnit(value, "mg", "mg", state))
    }

    @Test
    fun convertBlankUnitsAreTreatedAsUnity() {
        assertEquals(0, convert("5", "", "1")!!.compareTo(BigDecimal("5")))
    }

    @Test
    fun convertMilligramsToGrams() {
        assertEquals(0, convert("5", "mg", "g")!!.compareTo(BigDecimal("0.005")))
    }

    @Test
    fun convertIncompatibleUnitsReturnsNull() {
        assertNull(convert("5", "mg", "day"))
    }

    @Test
    fun convertUnparseableUnitReturnsNull() {
        // UCUM throws for an unknown unit; convertUnit swallows the exception and yields null.
        assertNull(convert("1", "xyz", "m"))
    }

    // ---------- convertToMostGranularCommeasurableUnit ----------

    @Test
    fun sameUnitsAreReturnedAsIs() {
        val (first, second) =
            UnitUtils.convertToMostGranularCommeasurableUnit(
                BigDecimal("5"),
                "mg",
                BigDecimal("3"),
                "mg",
                state,
            )
        assertEquals(BigDecimal("5"), first.first)
        assertEquals("mg", first.second)
        assertEquals(BigDecimal("3"), second.first)
        assertEquals("mg", second.second)
    }

    @Test
    fun convertsToSmallerUnitWhenFirstOperandIsLarger() {
        // 1 g and 500 mg -> 1000 mg and 500 mg (the more granular unit wins)
        val (first, second) =
            UnitUtils.convertToMostGranularCommeasurableUnit(
                BigDecimal("1"),
                "g",
                BigDecimal("500"),
                "mg",
                state,
            )
        assertEquals(0, first.first.compareTo(BigDecimal("1000")))
        assertEquals("mg", first.second)
        assertEquals(0, second.first.compareTo(BigDecimal("500")))
        assertEquals("mg", second.second)
    }

    @Test
    fun convertsSecondOperandWhenItIsLarger() {
        // 500 mg and 1 g -> 500 mg and 1000 mg
        val (first, second) =
            UnitUtils.convertToMostGranularCommeasurableUnit(
                BigDecimal("500"),
                "mg",
                BigDecimal("1"),
                "g",
                state,
            )
        assertEquals(0, first.first.compareTo(BigDecimal("500")))
        assertEquals("mg", first.second)
        assertEquals(0, second.first.compareTo(BigDecimal("1000")))
        assertEquals("mg", second.second)
    }

    @Test
    fun incompatibleUnitsAreReturnedUnconverted() {
        val (first, second) =
            UnitUtils.convertToMostGranularCommeasurableUnit(
                BigDecimal("5"),
                "mg",
                BigDecimal("2"),
                "day",
                state,
            )
        assertEquals(BigDecimal("5"), first.first)
        assertEquals("mg", first.second)
        assertEquals(BigDecimal("2"), second.first)
        assertEquals("day", second.second)
    }
}
