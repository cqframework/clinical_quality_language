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
        var value = results.forExpression("Null")!!.value
        Assertions.assertNull(value)
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("BooleanFalse")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("BooleanTrue")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerZero")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("IntegerPosZero")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+0))

        value = results.forExpression("IntegerNegZero")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-0))

        value = results.forExpression("IntegerOne")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("IntegerPosOne")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+1))

        value = results.forExpression("IntegerNegOne")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results.forExpression("IntegerTwo")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("IntegerPosTwo")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+2))

        value = results.forExpression("IntegerNegTwo")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2))

        value = results.forExpression("Integer10Pow9")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(10.0.pow(9.0).toInt()))

        value = results.forExpression("IntegerPos10Pow9")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+1 * 10.0.pow(9.0).toInt()))

        value = results.forExpression("IntegerNeg10Pow9")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1 * 10.0.pow(9.0).toInt()))

        value = results.forExpression("Integer2Pow31ToZero1IntegerMaxValue")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`((2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        ) // Power(2,30)-1+Power(2,30)

        value = results.forExpression("IntegerPos2Pow31ToZero1IntegerMaxValue")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(+2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(+1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        )

        value = results.forExpression("IntegerNeg2Pow31ToZero1")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2147483647))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(-1 * (2.0.pow(30.0) - 1 + 2.0.pow(30.0)).toInt()),
        )

        value = results.forExpression("IntegerNeg2Pow31IntegerMinValue")!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-2147483648))
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(-1 * (2.0.pow(30.0)).toInt() - 1 * (2.0.pow(30.0)).toInt()),
        )

        value = results.forExpression("QuantityZero")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantityPosZero")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantityNegZero")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(0)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantityOne")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(1)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantityPosOne")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat((value as Quantity).value, Matchers.comparesEqualTo(BigDecimal(1)))
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantityNegOne")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0).subtract(BigDecimal(1))),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("g"))

        value = results.forExpression("QuantitySmall")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("QuantityPosSmall")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("QuantityNegSmall")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(
                BigDecimal(0).subtract(BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))
            ),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("QuantityStep")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("QuantityPosStep")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("QuantityNegStep")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(
                BigDecimal(0).subtract(BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))
            ),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityMax: 99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityMax")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityPosMax: +99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityPosMax")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        // define QuantityMin: -99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityMin")!!.value
        MatcherAssert.assertThat(value, Matchers.instanceOf(Quantity::class.java))
        MatcherAssert.assertThat(
            (value as Quantity).value,
            Matchers.comparesEqualTo(BigDecimal("-99999999999999999999.99999999")),
        )
        MatcherAssert.assertThat(value.unit, Matchers.`is`("mg"))

        value = results.forExpression("DecimalZero")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        value = results.forExpression("DecimalPosZero")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results.forExpression("DecimalNegZero")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.0)),
        )

        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new
        // BigDecimal(42.0))));
        value = results.forExpression("DecimalOne")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal.ONE),
        )

        value = results.forExpression("DecimalPosOne")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal.ONE),
        )

        value = results.forExpression("DecimalNegOne")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-1.0)),
        )

        value = results.forExpression("DecimalTwo")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2.0)),
        )

        value = results.forExpression("DecimalPosTwo")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2.0)),
        )

        value = results.forExpression("DecimalNegTwo")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2.0)),
        )

        value = results.forExpression("Decimal10Pow9")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(9.0))),
        )

        value = results.forExpression("DecimalNeg10Pow9")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal((-10.0).pow(9.0))),
        )

        value = results.forExpression("Decimal2Pow31ToZero1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(2.0.pow(30.0) - 1 + 2.0.pow(30.0))),
        )

        value = results.forExpression("DecimalPos2Pow31ToZero1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(2.0.pow(30.0) - 1 + 2.0.pow(30.0))),
        )

        value = results.forExpression("DecimalNeg2Pow31ToZero1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483647.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-1 * (2.0.pow(30.0)) + 1 - 1 * (2.0.pow(30.0)))),
        )

        value = results.forExpression("Decimal2Pow31")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + (2.0.pow(30.0)))),
        )

        value = results.forExpression("DecimalPos2Pow31")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + (2.0.pow(30.0)))),
        )

        value = results.forExpression("DecimalNeg2Pow31")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483648.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-(2.0.pow(30.0)) - (2.0.pow(30.0)))),
        )

        value = results.forExpression("Decimal2Pow31ToInf1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + 1.0 + (2.0.pow(30.0)))),
        )

        value = results.forExpression("DecimalPos2Pow31ToInf1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal((2.0.pow(30.0)) + 1.0 + (2.0.pow(30.0)))),
        )

        value = results.forExpression("DecimalNeg2Pow31ToInf1")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-2147483649.0)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(-(2.0.pow(30.0)) - 1.0 - (2.0.pow(30.0)))),
        )

        value = results.forExpression("DecimalZeroStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results.forExpression("DecimalPosZeroStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results.forExpression("DecimalNegZeroStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            value as BigDecimal?,
            Matchers.comparesEqualTo(BigDecimal(-0.00000000)),
        )
        MatcherAssert.assertThat(
            value,
            Matchers.comparesEqualTo(BigDecimal(42.0).subtract(BigDecimal(42.0))),
        )

        value = results.forExpression("DecimalOneStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalPosOneStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalNegOneStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-1 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalTwoStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalPosTwoStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalNegTwoStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-2 * 10.0.pow(-8.0)).setScale(8, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalTenStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalPosTenStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal(10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        value = results.forExpression("DecimalNegTenStep")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(
                BigDecimal(-1 * 10.0.pow(-7.0)).setScale(7, RoundingMode.HALF_EVEN)
            ),
        )

        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        // define DecimalMaxValue : 99999999999999999999.99999999
        value = results.forExpression("DecimalMaxValue")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        // define DecimalPosMaxValue : +99999999999999999999.99999999
        value = results.forExpression("DecimalPosMaxValue")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("99999999999999999999.99999999")),
        )
        // define DecimalMinValue: -99999999999999999999.99999999
        value = results.forExpression("DecimalMinValue")!!.value
        MatcherAssert.assertThat<BigDecimal?>(
            (value as BigDecimal?),
            Matchers.comparesEqualTo(BigDecimal("-99999999999999999999.99999999")),
        )
    }
}
