package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlValueLiteralsAndSelectorsTest extends CqlExecutionTestBase {


    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NullEvaluator#evaluate(Context)}
     */
    @Test
    public void testNull() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Null").getExpression().evaluate(context);
        Assert.assertNull(result);
        assertThat(result, is(nullValue()));

    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator#evaluate(Context)}
     */
    @Test
    public void testBoolean() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("BooleanFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("BooleanTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LiteralEvaluator#evaluate(Context)}
     */
    @Test
    public void testInteger() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerZero").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("IntegerPosZero").getExpression().evaluate(context);
        assertThat(result, is(+0));

        result = context.resolveExpressionRef("IntegerNegZero").getExpression().evaluate(context);
        assertThat(result, is(-0));

        result = context.resolveExpressionRef("IntegerOne").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("IntegerPosOne").getExpression().evaluate(context);
        assertThat(result, is(+1));

        result = context.resolveExpressionRef("IntegerNegOne").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("IntegerTwo").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("IntegerPosTwo").getExpression().evaluate(context);
        assertThat(result, is(+2));

        result = context.resolveExpressionRef("IntegerNegTwo").getExpression().evaluate(context);
        assertThat(result, is(-2));

        result = context.resolveExpressionRef("Integer10Pow9").getExpression().evaluate(context);
        assertThat(result, is((int)Math.pow(10,9)));

        result = context.resolveExpressionRef("IntegerPos10Pow9").getExpression().evaluate(context);
        assertThat(result, is(+1*(int)Math.pow(10,9)));

        result = context.resolveExpressionRef("IntegerNeg10Pow9").getExpression().evaluate(context);
        assertThat(result, is(-1*(int)Math.pow(10,9)));

        result = context.resolveExpressionRef("Integer2Pow31ToZero1IntegerMaxValue").getExpression().evaluate(context);
        assertThat(result, is(2147483647));
        assertThat(result, is((int)(Math.pow(2,30) -1 + Math.pow(2,30)))); //Power(2,30)-1+Power(2,30)

        result = context.resolveExpressionRef("IntegerPos2Pow31ToZero1IntegerMaxValue").getExpression().evaluate(context);
        assertThat(result, is(+2147483647));
        assertThat(result, is(+1* (int)(Math.pow(2,30) -1 + Math.pow(2,30))));

        result = context.resolveExpressionRef("IntegerNeg2Pow31ToZero1").getExpression().evaluate(context);
        assertThat(result, is(-2147483647));
        assertThat(result, is(-1* (int)(Math.pow(2,30) -1 + Math.pow(2,30))));

        try {
            context.resolveExpressionRef("Integer2Pow31").getExpression().evaluate(context);
        } catch (CqlException ex) {

        }

        try {
            context.resolveExpressionRef("IntegerPos2Pow31").getExpression().evaluate(context);
        } catch (CqlException ex) {

        }

        result = context.resolveExpressionRef("IntegerNeg2Pow31IntegerMinValue").getExpression().evaluate(context);
        assertThat(result, is(-2147483648));
        assertThat(result, is(-1* (int)(Math.pow(2,30) ) -1* (int)(Math.pow(2,30) )));

        try {
            context.resolveExpressionRef("Integer2Pow31ToInf1").getExpression().evaluate(context);
        } catch (CqlException ex) {
        }

        try {
            context.resolveExpressionRef("IntegerPos2Pow31ToInf1").getExpression().evaluate(context);
        } catch (CqlException ex) {
        }

        try {
            context.resolveExpressionRef("IntegerNeg2Pow31ToInf1").getExpression().evaluate(context);
        } catch (CqlException ex) {
            //System.out.println(ex.toString());
        }


    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.QuantityEvaluator#evaluate(Context)}
     */
    @Test
    public void testQuantity() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("QuantityZero").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantityPosZero").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantityNegZero").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantityOne").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantityPosOne").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantityNegOne").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(1))));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("QuantitySmall").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = context.resolveExpressionRef("QuantityPosSmall").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = context.resolveExpressionRef("QuantityNegSmall").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = context.resolveExpressionRef("QuantityStep").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = context.resolveExpressionRef("QuantityPosStep").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN)));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        result = context.resolveExpressionRef("QuantityNegStep").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(0).subtract(new BigDecimal(0.00000001).setScale(8, RoundingMode.HALF_EVEN))));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityMax: 9999999999999999999999999999.99999999 'mg'
        result = context.resolveExpressionRef("QuantityMax").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityPosMax: +9999999999999999999999999999.99999999 'mg'
        result = context.resolveExpressionRef("QuantityPosMax").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

        //define QuantityMin: -9999999999999999999999999999.99999999 'mg'
        result = context.resolveExpressionRef("QuantityMin").getExpression().evaluate(context);
        assertThat(result, instanceOf(Quantity.class));
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));
        assertThat(((Quantity)result).getUnit(), is("mg"));

    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LiteralEvaluator#evaluate(Context)}
     */
    @Test
    public void testDecimal() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("DecimalZero").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = context.resolveExpressionRef("DecimalPosZero").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));
        //assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = context.resolveExpressionRef("DecimalNegZero").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));
        //assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = context.resolveExpressionRef("DecimalOne").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.ONE));

        result = context.resolveExpressionRef("DecimalPosOne").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.ONE));

        result = context.resolveExpressionRef("DecimalNegOne").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-1)));

        result = context.resolveExpressionRef("DecimalTwo").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = context.resolveExpressionRef("DecimalPosTwo").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = context.resolveExpressionRef("DecimalNegTwo").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-2)));

        result = context.resolveExpressionRef("Decimal10Pow9").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(10.0, 9.0))));

        result = context.resolveExpressionRef("DecimalNeg10Pow9").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-Math.pow(10.0, 9.0))));

        result = context.resolveExpressionRef("Decimal2Pow31ToZero1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(2.0,30.0) -1 + Math.pow(2.0,30.0))));

        result = context.resolveExpressionRef("DecimalPos2Pow31ToZero1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.pow(2.0,30.0) -1 + Math.pow(2.0,30.0))));

        result = context.resolveExpressionRef("DecimalNeg2Pow31ToZero1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483647.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1*(Math.pow(2.0,30.0)) +1 -1*(Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("Decimal2Pow31").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("DecimalPos2Pow31").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("DecimalNeg2Pow31").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483648.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(Math.pow(2.0,30.0)) - (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("Decimal2Pow31ToInf1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + 1.0 + (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("DecimalPos2Pow31ToInf1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((Math.pow(2.0,30.0)) + 1.0 + (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("DecimalNeg2Pow31ToInf1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2147483649.0)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(Math.pow(2.0,30.0)) -1.0 - (Math.pow(2.0,30.0)))));

        result = context.resolveExpressionRef("DecimalZeroStep").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = context.resolveExpressionRef("DecimalPosZeroStep").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = context.resolveExpressionRef("DecimalNegZeroStep").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-0.00000000)));
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(42.0).subtract(new BigDecimal(42.0))));

        result = context.resolveExpressionRef("DecimalOneStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalPosOneStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalNegOneStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-1 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalTwoStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalPosTwoStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalNegTwoStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(8, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-2 * Math.pow(10.0,-8)).setScale(8,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalTenStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalPosTenStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        result = context.resolveExpressionRef("DecimalNegTenStep").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));
        //assertThat(((BigDecimal)result).setScale(7, RoundingMode.HALF_EVEN), comparesEqualTo(new BigDecimal(-1*Math.pow(10.0,-7)).setScale(7,RoundingMode.HALF_EVEN)));

        //define DecimalMaxValue : 9999999999999999999999999999.99999999
        result = context.resolveExpressionRef("DecimalMaxValue").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        //define DecimalPosMaxValue : +9999999999999999999999999999.99999999
        result = context.resolveExpressionRef("DecimalPosMaxValue").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("9999999999999999999999999999.99999999")));
        //define DecimalMinValue: -9999999999999999999999999999.99999999
        result = context.resolveExpressionRef("DecimalMinValue").getExpression().evaluate(context);
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("-9999999999999999999999999999.99999999")));

//        try {
//            context.resolveExpressionRef("DecimalTenthStep").getExpression().evaluate(context);
//        } catch (CqlException ex) {
//
//        }

//        try {
//            context.resolveExpressionRef("DecimalPosTenthStep").getExpression().evaluate(context);
//        } catch (CqlException ex) {
//
//        }
//
//        try {
//            context.resolveExpressionRef("DecimalNegTenthStep").getExpression().evaluate(context);
//        } catch (CqlException ex) {
//
//        }





    }

}
