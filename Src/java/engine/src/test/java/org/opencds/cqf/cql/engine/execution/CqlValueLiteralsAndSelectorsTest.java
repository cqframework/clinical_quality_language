package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;


public class CqlValueLiteralsAndSelectorsTest extends CqlTestBase {

    @Test
    public void test_all_ValueLiteralsAndSelectors() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlValueLiteralsAndSelectorsTest"), null, null, null, null, null);
        Object result;

        result = evaluationResult.expressionResults.get("Null").value();
        Assert.assertNull(result);
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("BooleanFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("BooleanTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerZero").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("IntegerPosZero").value();
        assertThat(result, is(+0));

        result = evaluationResult.expressionResults.get("IntegerNegZero").value();
        assertThat(result, is(-0));

        result = evaluationResult.expressionResults.get("IntegerOne").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("IntegerPosOne").value();
        assertThat(result, is(+1));

        result = evaluationResult.expressionResults.get("IntegerNegOne").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("IntegerTwo").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("IntegerPosTwo").value();
        assertThat(result, is(+2));

        result = evaluationResult.expressionResults.get("IntegerNegTwo").value();
        assertThat(result, is(-2));

        result = evaluationResult.expressionResults.get("Integer10Pow9").value();
        assertThat(result, is((int)Math.pow(10,9)));

        result = evaluationResult.expressionResults.get("IntegerPos10Pow9").value();
        assertThat(result, is(+1*(int)Math.pow(10,9)));

        result = evaluationResult.expressionResults.get("IntegerNeg10Pow9").value();
        assertThat(result, is(-1*(int)Math.pow(10,9)));

        result = evaluationResult.expressionResults.get("Integer2Pow31ToZero1IntegerMaxValue").value();
        assertThat(result, is(2147483647));
        assertThat(result, is((int)(Math.pow(2,30) -1 + Math.pow(2,30)))); //Power(2,30)-1+Power(2,30)

        result = evaluationResult.expressionResults.get("IntegerPos2Pow31ToZero1IntegerMaxValue").value();
        assertThat(result, is(+2147483647));
        assertThat(result, is(+1* (int)(Math.pow(2,30) -1 + Math.pow(2,30))));

        result = evaluationResult.expressionResults.get("IntegerNeg2Pow31ToZero1").value();
        assertThat(result, is(-2147483647));
        assertThat(result, is(-1* (int)(Math.pow(2,30) -1 + Math.pow(2,30))));


        result = evaluationResult.expressionResults.get("IntegerNeg2Pow31IntegerMinValue").value();
        assertThat(result, is(-2147483648));
        assertThat(result, is(-1* (int)(Math.pow(2,30) ) -1* (int)(Math.pow(2,30) )));

        result = evaluationResult.expressionResults.get("QuantityZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantityPosZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantityNegZero").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantityOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantityPosOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantityNegOne").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(1))));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("QuantitySmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("QuantityPosSmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("QuantityNegSmall").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("QuantityStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("QuantityPosStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("QuantityNegStep").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityMax: 9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.expressionResults.get("QuantityMax").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityPosMax: +9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.expressionResults.get("QuantityPosMax").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityMin: -9999999999999999999999999999.99999999 'mg'
        result = evaluationResult.expressionResults.get("QuantityMin").value();
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = evaluationResult.expressionResults.get("DecimalZero").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.expressionResults.get("DecimalPosZero").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));
        //assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.expressionResults.get("DecimalNegZero").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));
        //assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.expressionResults.get("DecimalOne").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.ONE));

        result = evaluationResult.expressionResults.get("DecimalPosOne").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.ONE));

        result = evaluationResult.expressionResults.get("DecimalNegOne").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-1)));

        result = evaluationResult.expressionResults.get("DecimalTwo").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.expressionResults.get("DecimalPosTwo").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.expressionResults.get("DecimalNegTwo").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-2)));

        result = evaluationResult.expressionResults.get("Decimal10Pow9").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(10.0, 9.0))));

        result = evaluationResult.expressionResults.get("DecimalNeg10Pow9").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-Math.pow(10.0, 9.0))));

        result = evaluationResult.expressionResults.get("Decimal2Pow31ToZero1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(2.0,30.0) -1 + Math.pow(2.0,30.0))));

        result = evaluationResult.expressionResults.get("DecimalPos2Pow31ToZero1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(2.0,30.0) -1 + Math.pow(2.0,30.0))));

        result = evaluationResult.expressionResults.get("DecimalNeg2Pow31ToZero1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1*(Math.pow(2.0,30.0)) +1 -1*(Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("Decimal2Pow31").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("DecimalPos2Pow31").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("DecimalNeg2Pow31").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(Math.pow(2.0,30.0)) - (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("Decimal2Pow31ToInf1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + 1.0 + (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("DecimalPos2Pow31ToInf1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + 1.0 + (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("DecimalNeg2Pow31ToInf1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(Math.pow(2.0,30.0)) -1.0 - (Math.pow(2.0,30.0)))));

        result = evaluationResult.expressionResults.get("DecimalZeroStep").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.expressionResults.get("DecimalPosZeroStep").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.expressionResults.get("DecimalNegZeroStep").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = evaluationResult.expressionResults.get("DecimalOneStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalPosOneStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalNegOneStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalTwoStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalPosTwoStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalNegTwoStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalTenStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalPosTenStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = evaluationResult.expressionResults.get("DecimalNegTenStep").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        //define DecimalMaxValue : 9999999999999999999999999999.99999999
        result = evaluationResult.expressionResults.get("DecimalMaxValue").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        //define DecimalPosMaxValue : +9999999999999999999999999999.99999999
        result = evaluationResult.expressionResults.get("DecimalPosMaxValue").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        //define DecimalMinValue: -9999999999999999999999999999.99999999
        result = evaluationResult.expressionResults.get("DecimalMinValue").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));



    }
}
