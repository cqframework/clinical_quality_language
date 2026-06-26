package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class CqlValueLiteralsAndSelectorsTest : CqlTestBase() {
    @Test
    fun all_value_literals_and_selectors() {
        val results =
            engine.evaluate { library("CqlValueLiteralsAndSelectorsTest") }.onlyResultOrThrow
        var value = results["Null"]!!.value
        assertNull(value)

        value = results["BooleanFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["BooleanTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerZero"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["IntegerPosZero"]!!.value
        assertEquals((+0).toCqlInteger(), value)

        value = results["IntegerNegZero"]!!.value
        assertEquals((-0).toCqlInteger(), value)

        value = results["IntegerOne"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["IntegerPosOne"]!!.value
        assertEquals((+1).toCqlInteger(), value)

        value = results["IntegerNegOne"]!!.value
        assertEquals((-1).toCqlInteger(), value)

        value = results["IntegerTwo"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["IntegerPosTwo"]!!.value
        assertEquals((+2).toCqlInteger(), value)

        value = results["IntegerNegTwo"]!!.value
        assertEquals((-2).toCqlInteger(), value)

        value = results["Integer10Pow9"]!!.value
        assertEquals(10.0.pow(9.0).toInt().toCqlInteger(), value)

        value = results["IntegerPos10Pow9"]!!.value
        assertEquals((+1 * 10.0.pow(9.0)).toInt().toCqlInteger(), value)

        value = results["IntegerNeg10Pow9"]!!.value
        assertEquals((-1 * 10.0.pow(9.0).toInt()).toCqlInteger(), value)

        value = results["Integer2Pow31ToZero1IntegerMaxValue"]!!.value
        assertEquals(2147483647.toCqlInteger(), value)
        assertEquals(
            (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt().toCqlInteger(),
            value,
        ) // Power(2,30)-1+Power(2,30)

        value = results["IntegerPos2Pow31ToZero1IntegerMaxValue"]!!.value
        assertEquals((+2147483647).toCqlInteger(), value)
        assertEquals((+1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()).toCqlInteger(), value)

        value = results["IntegerNeg2Pow31ToZero1"]!!.value
        assertEquals((-2147483647).toCqlInteger(), value)
        assertEquals((-1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()).toCqlInteger(), value)

        value = results["IntegerNeg2Pow31IntegerMinValue"]!!.value
        assertEquals((-2147483648).toCqlInteger(), value)
        assertEquals(
            (-1 * (2.0.pow(30.0)).toInt() - 1 * (2.0.pow(30.0)).toInt()).toCqlInteger(),
            value,
        )

        value = results["QuantityZero"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0), value.value)
        assertEquals("g", value.unit)

        value = results["QuantityPosZero"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0), value.value)
        assertEquals("g", value.unit)

        value = results["QuantityNegZero"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0), value.value)
        assertEquals("g", value.unit)

        value = results["QuantityOne"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("1.0"), value.value)
        assertEquals("g", value.unit)

        value = results["QuantityPosOne"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("1.0"), value.value)
        assertEquals("g", value.unit)

        value = results["QuantityNegOne"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("-1.0"), value.value)
        assertEquals("g", value.unit)

        value = results["QuantitySmall"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        value = results["QuantityPosSmall"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        value = results["QuantityNegSmall"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(-0.05).setScale(2, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        value = results["QuantityStep"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        value = results["QuantityPosStep"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        value = results["QuantityNegStep"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal(-0.00000001).setScale(8, RoundingMode.HALF_EVEN), value.value)
        assertEquals("mg", value.unit)

        // define QuantityMax: 99999999999999999999.99999999 'mg'
        value = results["QuantityMax"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("99999999999999999999.99999999"), value.value)
        assertEquals("mg", value.unit)

        // define QuantityPosMax: +99999999999999999999.99999999 'mg'
        value = results["QuantityPosMax"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("99999999999999999999.99999999"), value.value)
        assertEquals("mg", value.unit)

        // define QuantityMin: -99999999999999999999.99999999 'mg'
        value = results["QuantityMin"]!!.value
        assertIs<Quantity>(value)
        assertEquals(BigDecimal("-99999999999999999999.99999999"), value.value)
        assertEquals("mg", value.unit)

        value = results["DecimalZero"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("0.0"), value.value)

        value = results["DecimalPosZero"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("0.0"), value.value)

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results["DecimalNegZero"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("0.0"), value.value)

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results["DecimalOne"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("1.0"), value.value)

        value = results["DecimalPosOne"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("1.0"), value.value)

        value = results["DecimalNegOne"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-1.0"), value.value)

        value = results["DecimalTwo"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2.0"), value.value)

        value = results["DecimalPosTwo"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2.0"), value.value)

        value = results["DecimalNegTwo"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-2.0"), value.value)

        value = results["Decimal10Pow9"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("1000000000.0"), value.value)

        value = results["DecimalNeg10Pow9"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-1000000000.0"), value.value)

        value = results["Decimal2Pow31ToZero1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483647.0"), value.value)

        value = results["DecimalPos2Pow31ToZero1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483647.0"), value.value)

        value = results["DecimalNeg2Pow31ToZero1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-2147483647.0"), value.value)

        value = results["Decimal2Pow31"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483648.0"), value.value)

        value = results["DecimalPos2Pow31"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483648.0"), value.value)

        value = results["DecimalNeg2Pow31"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-2147483648.0"), value.value)

        value = results["Decimal2Pow31ToInf1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483649.0"), value.value)

        value = results["DecimalPos2Pow31ToInf1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("2147483649.0"), value.value)

        value = results["DecimalNeg2Pow31ToInf1"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-2147483649.0"), value.value)

        value = results["DecimalZeroStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("0.00000000"), value.value)

        value = results["DecimalPosZeroStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("0.00000000"), value.value)

        value = results["DecimalNegZeroStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-0.00000000"), value.value)

        value = results["DecimalOneStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN), value.value)

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosOneStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN), value.value)

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegOneStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(
            BigDecimal(-1 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN),
            value.value,
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalTwoStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(
            BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN),
            value.value,
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosTwoStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(
            BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN),
            value.value,
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegTwoStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(
            BigDecimal(-2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN),
            value.value,
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalTenStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN), value.value)

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosTenStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN), value.value)

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegTenStep"]!!.value
        assertIs<Decimal>(value)
        assertEquals(
            BigDecimal(-1 * 10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN),
            value.value,
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        // define DecimalMaxValue : 99999999999999999999.99999999
        value = results["DecimalMaxValue"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("99999999999999999999.99999999"), value.value)

        // define DecimalPosMaxValue : +99999999999999999999.99999999
        value = results["DecimalPosMaxValue"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("99999999999999999999.99999999"), value.value)

        // define DecimalMinValue: -99999999999999999999.99999999
        value = results["DecimalMinValue"]!!.value
        assertIs<Decimal>(value)
        assertEquals(BigDecimal("-99999999999999999999.99999999"), value.value)
    }
}
