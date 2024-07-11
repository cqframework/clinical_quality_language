package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.*;

class CqlValueLiteralsAndSelectorsTest extends CqlTestBase {

    @Test
    void all_value_literals_and_selectors() {
        var results = engine.evaluate(toElmIdentifier("CqlValueLiteralsAndSelectorsTest"));
        var value = results.forExpression("Null").value();
        assertNull(value);
        assertThat(value, is(nullValue()));

        value = results.forExpression("BooleanFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("BooleanTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerZero").value();
        assertThat(value, is(0));

        value = results.forExpression("IntegerPosZero").value();
        assertThat(value, is(+0));

        value = results.forExpression("IntegerNegZero").value();
        assertThat(value, is(-0));

        value = results.forExpression("IntegerOne").value();
        assertThat(value, is(1));

        value = results.forExpression("IntegerPosOne").value();
        assertThat(value, is(+1));

        value = results.forExpression("IntegerNegOne").value();
        assertThat(value, is(-1));

        value = results.forExpression("IntegerTwo").value();
        assertThat(value, is(2));

        value = results.forExpression("IntegerPosTwo").value();
        assertThat(value, is(+2));

        value = results.forExpression("IntegerNegTwo").value();
        assertThat(value, is(-2));

        value = results.forExpression("Integer10Pow9").value();
        assertThat(value, is((int) Math.pow(10, 9)));

        value = results.forExpression("IntegerPos10Pow9").value();
        assertThat(value, is(+1 * (int) Math.pow(10, 9)));

        value = results.forExpression("IntegerNeg10Pow9").value();
        assertThat(value, is(-1 * (int) Math.pow(10, 9)));

        value = results.forExpression("Integer2Pow31ToZero1IntegerMaxValue").value();
        assertThat(value, is(2147483647));
        assertThat(value, is((int) (Math.pow(2, 30) - 1 + Math.pow(2, 30)))); // Power(2,30)-1+Power(2,30)

        value = results.forExpression("IntegerPos2Pow31ToZero1IntegerMaxValue").value();
        assertThat(value, is(+2147483647));
        assertThat(value, is(+1 * (int) (Math.pow(2, 30) - 1 + Math.pow(2, 30))));

        value = results.forExpression("IntegerNeg2Pow31ToZero1").value();
        assertThat(value, is(-2147483647));
        assertThat(value, is(-1 * (int) (Math.pow(2, 30) - 1 + Math.pow(2, 30))));

        value = results.forExpression("IntegerNeg2Pow31IntegerMinValue").value();
        assertThat(value, is(-2147483648));
        assertThat(value, is(-1 * (int) (Math.pow(2, 30)) - 1 * (int) (Math.pow(2, 30))));

        value = results.forExpression("QuantityZero").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantityPosZero").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantityNegZero").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantityOne").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantityPosOne").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantityNegOne").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(1))));
        assertThat(((Quantity) value).getUnit(), is("g"));

        value = results.forExpression("QuantitySmall").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("QuantityPosSmall").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("QuantityNegSmall").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("QuantityStep").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("QuantityPosStep").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("QuantityNegStep").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) value).getValue(),
                comparesEqualTo(
                        new BigDecimal(0).subtract(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        // define QuantityMax: 99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityMax").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal("99999999999999999999.99999999")));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        // define QuantityPosMax: +99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityPosMax").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal("99999999999999999999.99999999")));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        // define QuantityMin: -99999999999999999999.99999999 'mg'
        value = results.forExpression("QuantityMin").value();
        assertThat(value, instanceOf(Quantity.class));
        assertThat(((Quantity) value).getValue(), comparesEqualTo(new BigDecimal("-99999999999999999999.99999999")));
        assertThat(((Quantity) value).getUnit(), is("mg"));

        value = results.forExpression("DecimalZero").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(0.0)));

        value = results.forExpression("DecimalPosZero").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(0.0)));
        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        value = results.forExpression("DecimalNegZero").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(0.0)));
        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        value = results.forExpression("DecimalOne").value();
        assertThat((BigDecimal) value, comparesEqualTo(BigDecimal.ONE));

        value = results.forExpression("DecimalPosOne").value();
        assertThat((BigDecimal) value, comparesEqualTo(BigDecimal.ONE));

        value = results.forExpression("DecimalNegOne").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal((double) -1)));

        value = results.forExpression("DecimalTwo").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2.0)));

        value = results.forExpression("DecimalPosTwo").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2.0)));

        value = results.forExpression("DecimalNegTwo").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal((double) -2)));

        value = results.forExpression("Decimal10Pow9").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(Math.pow(10.0, 9.0))));

        value = results.forExpression("DecimalNeg10Pow9").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-Math.pow(10.0, 9.0))));

        value = results.forExpression("Decimal2Pow31ToZero1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(Math.pow(2.0, 30.0) - 1 + Math.pow(2.0, 30.0))));

        value = results.forExpression("DecimalPos2Pow31ToZero1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(Math.pow(2.0, 30.0) - 1 + Math.pow(2.0, 30.0))));

        value = results.forExpression("DecimalNeg2Pow31ToZero1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-2147483647.0)));
        assertThat(
                (BigDecimal) value,
                comparesEqualTo(new BigDecimal(-1 * (Math.pow(2.0, 30.0)) + 1 - 1 * (Math.pow(2.0, 30.0)))));

        value = results.forExpression("Decimal2Pow31").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + (Math.pow(2.0, 30.0)))));

        value = results.forExpression("DecimalPos2Pow31").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + (Math.pow(2.0, 30.0)))));

        value = results.forExpression("DecimalNeg2Pow31").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-2147483648.0)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-(Math.pow(2.0, 30.0)) - (Math.pow(2.0, 30.0)))));

        value = results.forExpression("Decimal2Pow31ToInf1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat(
                (BigDecimal) value,
                comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + 1.0 + (Math.pow(2.0, 30.0)))));

        value = results.forExpression("DecimalPos2Pow31ToInf1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat(
                (BigDecimal) value,
                comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + 1.0 + (Math.pow(2.0, 30.0)))));

        value = results.forExpression("DecimalNeg2Pow31ToInf1").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-2147483649.0)));
        assertThat(
                (BigDecimal) value,
                comparesEqualTo(new BigDecimal(-(Math.pow(2.0, 30.0)) - 1.0 - (Math.pow(2.0, 30.0)))));

        value = results.forExpression("DecimalZeroStep").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        value = results.forExpression("DecimalPosZeroStep").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        value = results.forExpression("DecimalNegZeroStep").value();
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(-0.00000000)));
        assertThat((BigDecimal) value, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        value = results.forExpression("DecimalOneStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalPosOneStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalNegOneStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalTwoStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalPosTwoStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalNegTwoStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalTenStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalPosTenStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        value = results.forExpression("DecimalNegTenStep").value();
        assertThat(
                ((BigDecimal) value),
                comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        // define DecimalMaxValue : 99999999999999999999.99999999
        value = results.forExpression("DecimalMaxValue").value();
        assertThat(((BigDecimal) value), comparesEqualTo(new BigDecimal("99999999999999999999.99999999")));
        // define DecimalPosMaxValue : +99999999999999999999.99999999
        value = results.forExpression("DecimalPosMaxValue").value();
        assertThat(((BigDecimal) value), comparesEqualTo(new BigDecimal("99999999999999999999.99999999")));
        // define DecimalMinValue: -99999999999999999999.99999999
        value = results.forExpression("DecimalMinValue").value();
        assertThat(((BigDecimal) value), comparesEqualTo(new BigDecimal("-99999999999999999999.99999999")));
    }
}
