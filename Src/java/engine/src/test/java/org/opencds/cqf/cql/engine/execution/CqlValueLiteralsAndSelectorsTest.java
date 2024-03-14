package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlValueLiteralsAndSelectorsTest extends CqlTestBase {

    @Test
    public void test_all_ValueLiteralsAndSelectors() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlValueLiteralsAndSelectorsTest"));
        Object result;

        result = evaluationResult.forExpression("Null").value();
        Assert.assertNull(result);
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("BooleanFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("BooleanTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerZero").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("IntegerPosZero").value();
        assertThat(result, is(+0));

        result = evaluationResult.forExpression("IntegerNegZero").value();
        assertThat(result, is(-0));

        result = evaluationResult.forExpression("IntegerOne").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("IntegerPosOne").value();
        assertThat(result, is(+1));

        result = evaluationResult.forExpression("IntegerNegOne").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("IntegerTwo").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("IntegerPosTwo").value();
        assertThat(result, is(+2));

        result = evaluationResult.forExpression("IntegerNegTwo").value();
        assertThat(result, is(-2));

        result = evaluationResult.forExpression("Integer10Pow9").value();
        assertThat(result, is((int) Math.pow(10, 9)));

        result = evaluationResult.forExpression("IntegerPos10Pow9").value();
        assertThat(result, is(+1 * (int) Math.pow(10, 9)));

        result = evaluationResult.forExpression("IntegerNeg10Pow9").value();
        assertThat(result, is(-1 * (int) Math.pow(10, 9)));

        result = evaluationResult
                .forExpression("Integer2Pow31ToZero1IntegerMaxValue")
                .value();
        assertThat(result, is(2147483647));
        assertThat(result, is((int) (Math.pow(2, 30) - 1 + Math.pow(2, 30)))); // Power(2,30)-1+Power(2,30)

        result = evaluationResult
                .forExpression("IntegerPos2Pow31ToZero1IntegerMaxValue")
                .value();
        assertThat(result, is(+2147483647));
        assertThat(result, is(+1 * (int) (Math.pow(2, 30) - 1 + Math.pow(2, 30))));

        result = evaluationResult.forExpression("IntegerNeg2Pow31ToZero1").value();
        assertThat(result, is(-2147483647));
        assertThat(result, is(-1 * (int) (Math.pow(2, 30) - 1 + Math.pow(2, 30))));

        result = evaluationResult
                .forExpression("IntegerNeg2Pow31IntegerMinValue")
                .value();
        assertThat(result, is(-2147483648));
        assertThat(result, is(-1 * (int) (Math.pow(2, 30)) - 1 * (int) (Math.pow(2, 30))));

        result = evaluationResult.forExpression("QuantityZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantityPosZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantityNegZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantityOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantityPosOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantityNegOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity) result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(1))));
        assertThat(((Quantity) result).getUnit(), is("g"));

        result = evaluationResult.forExpression("QuantitySmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("QuantityPosSmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("QuantityNegSmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("QuantityStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("QuantityPosStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("QuantityNegStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(
                        new BigDecimal(0).subtract(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        // define QuantityMax: 9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.forExpression("QuantityMax").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        // define QuantityPosMax: +9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.forExpression("QuantityPosMax").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        // define QuantityMin: -9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.forExpression("QuantityMin").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(
                ((Quantity) result).getValue(),
                comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));
        assertThat(((Quantity) result).getUnit(), is("mg"));

        result = evaluationResult.forExpression("DecimalZero").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.forExpression("DecimalPosZero").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(0.0)));
        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.forExpression("DecimalNegZero").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(0.0)));
        // assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.forExpression("DecimalOne").value();
        assertThat((BigDecimal) result, comparesEqualTo(BigDecimal.ONE));

        result = evaluationResult.forExpression("DecimalPosOne").value();
        assertThat((BigDecimal) result, comparesEqualTo(BigDecimal.ONE));

        result = evaluationResult.forExpression("DecimalNegOne").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal((double) -1)));

        result = evaluationResult.forExpression("DecimalTwo").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.forExpression("DecimalPosTwo").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.forExpression("DecimalNegTwo").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal((double) -2)));

        result = evaluationResult.forExpression("Decimal10Pow9").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(Math.pow(10.0, 9.0))));

        result = evaluationResult.forExpression("DecimalNeg10Pow9").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(-Math.pow(10.0, 9.0))));

        result = evaluationResult.forExpression("Decimal2Pow31ToZero1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(Math.pow(2.0, 30.0) - 1 + Math.pow(2.0, 30.0))));

        result = evaluationResult.forExpression("DecimalPos2Pow31ToZero1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(Math.pow(2.0, 30.0) - 1 + Math.pow(2.0, 30.0))));

        result = evaluationResult.forExpression("DecimalNeg2Pow31ToZero1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(-2147483647.0)));
        assertThat(
                (BigDecimal) result,
                comparesEqualTo(new BigDecimal(-1 * (Math.pow(2.0, 30.0)) + 1 - 1 * (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("Decimal2Pow31").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("DecimalPos2Pow31").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("DecimalNeg2Pow31").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(-2147483648.0)));
        assertThat(
                (BigDecimal) result, comparesEqualTo(new BigDecimal(-(Math.pow(2.0, 30.0)) - (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("Decimal2Pow31ToInf1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat(
                (BigDecimal) result,
                comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + 1.0 + (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("DecimalPos2Pow31ToInf1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat(
                (BigDecimal) result,
                comparesEqualTo(new BigDecimal((Math.pow(2.0, 30.0)) + 1.0 + (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("DecimalNeg2Pow31ToInf1").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(-2147483649.0)));
        assertThat(
                (BigDecimal) result,
                comparesEqualTo(new BigDecimal(-(Math.pow(2.0, 30.0)) - 1.0 - (Math.pow(2.0, 30.0)))));

        result = evaluationResult.forExpression("DecimalZeroStep").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.forExpression("DecimalPosZeroStep").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.forExpression("DecimalNegZeroStep").value();
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(-0.00000000)));
        assertThat((BigDecimal) result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.forExpression("DecimalOneStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalPosOneStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalNegOneStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalTwoStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalPosTwoStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalNegTwoStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0, -8)).setScale(8, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-2 *
        // Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalTenStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalPosTenStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = evaluationResult.forExpression("DecimalNegTenStep").value();
        assertThat(
                ((BigDecimal) result),
                comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0, -7)).setScale(7, RoundingMode.HALF_EVEN)));
        // assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new
        // BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        // define DecimalMaxValue : 9999999999999999999999999999.99999999
        result = evaluationResult.forExpression("DecimalMaxValue").value();
        assertThat(((BigDecimal) result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        // define DecimalPosMaxValue : +9999999999999999999999999999.99999999
        result = evaluationResult.forExpression("DecimalPosMaxValue").value();
        assertThat(((BigDecimal) result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        // define DecimalMinValue: -9999999999999999999999999999.99999999
        result = evaluationResult.forExpression("DecimalMinValue").value();
        assertThat(((BigDecimal) result), comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));
    }
}
