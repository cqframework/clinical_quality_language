package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.runtime.Quantity

internal class CqlValueLiteralsAndSelectorsTest : CqlTestBase() {
    @Test
    fun all_value_literals_and_selectors() {
        val results =
            engine.evaluate { library("CqlValueLiteralsAndSelectorsTest") }.onlyResultOrThrow
        var value = results["Null"]!!.value
        Assertions.assertNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["BooleanFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["BooleanTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["IntegerPosZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+0))

        value = results["IntegerNegZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-0))

        value = results["IntegerOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["IntegerPosOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+1))

        value = results["IntegerNegOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results["IntegerTwo"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["IntegerPosTwo"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+2))

        value = results["IntegerNegTwo"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2))

        value = results["Integer10Pow9"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(10.0.pow(9.0).toInt()))

        value = results["IntegerPos10Pow9"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+1 * 10.0.pow(9.0).toInt()))

        value = results["IntegerNeg10Pow9"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1 * 10.0.pow(9.0).toInt()))

        value = results["Integer2Pow31ToZero1IntegerMaxValue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`((2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        ) // Power(2,30)-1+Power(2,30)

        value = results["IntegerPos2Pow31ToZero1IntegerMaxValue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(+1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        )

        value = results["IntegerNeg2Pow31ToZero1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(-1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        )

        value = results["IntegerNeg2Pow31IntegerMinValue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2147483648))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(-1 * (2.0.pow(30.0)).toInt() - 1 * (2.0.pow(30.0)).toInt()),
        )

        value = results["QuantityZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantityPosZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantityNegZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantityOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(1)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantityPosOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(1)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantityNegOne"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0).subtract(BigDecimal(1))),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results["QuantitySmall"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["QuantityPosSmall"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["QuantityNegSmall"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(
                BigDecimal(0).subtract(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))
            ),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["QuantityStep"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["QuantityPosStep"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["QuantityNegStep"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(
                BigDecimal(0).subtract(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))
            ),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityMax: 99999999999999999999.99999999 'mg'
        value = results["QuantityMax"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityPosMax: +99999999999999999999.99999999 'mg'
        value = results["QuantityPosMax"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityMin: -99999999999999999999.99999999 'mg'
        value = results["QuantityMin"]!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("-99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results["DecimalZero"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        value = results["DecimalPosZero"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results["DecimalNegZero"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results["DecimalOne"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal.ONE),
        )

        value = results["DecimalPosOne"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal.ONE),
        )

        value = results["DecimalNegOne"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-1.0)),
        )

        value = results["DecimalTwo"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2.0)),
        )

        value = results["DecimalPosTwo"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2.0)),
        )

        value = results["DecimalNegTwo"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2.0)),
        )

        value = results["Decimal10Pow9"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(9.0))),
        )

        value = results["DecimalNeg10Pow9"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal((-10.0).pow(9.0))),
        )

        value = results["Decimal2Pow31ToZero1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(2.0.pow(30.0) - 1 + 2.0.pow(30.0))),
        )

        value = results["DecimalPos2Pow31ToZero1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(2.0.pow(30.0) - 1 + 2.0.pow(30.0))),
        )

        value = results["DecimalNeg2Pow31ToZero1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-1 * (2.0.pow(30.0)) + 1 - 1 * (2.0.pow(30.0)))),
        )

        value = results["Decimal2Pow31"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + (2.0.pow(30.0)))),
        )

        value = results["DecimalPos2Pow31"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + (2.0.pow(30.0)))),
        )

        value = results["DecimalNeg2Pow31"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-(2.0.pow(30.0)) - (2.0.pow(30.0)))),
        )

        value = results["Decimal2Pow31ToInf1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + 1.0 + (2.0.pow(30.0)))),
        )

        value = results["DecimalPos2Pow31ToInf1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + 1.0 + (2.0.pow(30.0)))),
        )

        value = results["DecimalNeg2Pow31ToInf1"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-(2.0.pow(30.0)) - 1.0 - (2.0.pow(30.0)))),
        )

        value = results["DecimalZeroStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results["DecimalPosZeroStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results["DecimalNegZeroStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results["DecimalOneStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosOneStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegOneStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-1 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalTwoStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosTwoStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegTwoStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results["DecimalTenStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results["DecimalPosTenStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results["DecimalNegTenStep"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-1 * 10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        // define DecimalMaxValue : 99999999999999999999.99999999
        value = results["DecimalMaxValue"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        // define DecimalPosMaxValue : +99999999999999999999.99999999
        value = results["DecimalPosMaxValue"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        // define DecimalMinValue: -99999999999999999999.99999999
        value = results["DecimalMinValue"]!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("-99999999999999999999.99999999")),
        )
    }
}
