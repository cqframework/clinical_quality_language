package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.elm.execution.AbsEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.AddEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Value;
import org.testng.Assert;
import org.testng.annotations.Test;


public class CqlArithmeticFunctionsTest extends CqlExecutionTestBase {
    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AbsEvaluator#evaluate(Context)}
     */
    @Test
    public void testAbs() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("AbsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Abs0").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("AbsNeg1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("AbsNeg1Long").getExpression().evaluate(context);
        assertThat(result, is(1L));

        result = context.resolveExpressionRef("AbsNeg1Dec").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("Abs0Dec").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = context.resolveExpressionRef("Abs1cm").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity)result).compareTo(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")) == 0);

        // error testing
        try {
            result = AbsEvaluator.abs("This is an error");
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AddEvaluator#evaluate(Context)}
     */
    @Test
    public void testAdd() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("AddNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Add11").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("Add12Long").getExpression().evaluate(context);
        assertThat(result, is(3L));

        result = context.resolveExpressionRef("Add1D1D").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("Add1Q1Q").getExpression().evaluate(context);
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("2"));
        Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        result = context.resolveExpressionRef("AddIAndD").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("3.0")));

        // error testing
        try {
            result = AddEvaluator.add("This is an error", 404);
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CeilingEvaluator#evaluate(Context)}
     */
    @Test
    public void testCeiling() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("CeilingNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Ceiling1D").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Ceiling1D1").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("CeilingNegD1").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("CeilingNeg1").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("CeilingNeg1D1").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("Ceiling1I").getExpression().evaluate(context);
        assertThat(result, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DivideEvaluator#evaluate(Context)}
     */
    @Test
    public void testDivide() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("DivideNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Divide10").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Divide01").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.0")));

        result = context.resolveExpressionRef("Divide11").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("Divide11Long").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("Divide1d1d").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("Divide103").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.33333333")));

        result = context.resolveExpressionRef("Divide1Q1").getExpression().evaluate(context);
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1"));
        Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        // TODO: The asserted "correct" answer 1.0'g/cm3' is wrong;
        // the true correct answer is just 1.0 with no units or empty string unit.
        // result = context.resolveExpressionRef("Divide1Q1Q").getExpression().evaluate(context);
        // Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1.0"));
        // Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        result = context.resolveExpressionRef("Divide10I5D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("Divide10I5I").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("Divide10Q5I").getExpression().evaluate(context);
        Assert.assertEquals(new BigDecimal("2.0"), ((Quantity) result).getValue());
        Assert.assertEquals("g", ((Quantity) result).getUnit());
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.FloorEvaluator#evaluate(Context)}
     */
    @Test
    public void testFloor() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("FloorNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Floor1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Floor1D").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Floor1D1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("FloorNegD1").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("FloorNeg1").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("FloorNeg1D1").getExpression().evaluate(context);
        assertThat(result, is(-2));

        result = context.resolveExpressionRef("Floor2I").getExpression().evaluate(context);
        assertThat(result, is(2));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ExpEvaluator#evaluate(Context)}
     */
    @Test
    public void testExp() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("ExpNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Exp0").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("ExpNeg0").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("Exp1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = context.resolveExpressionRef("Exp1Long").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = context.resolveExpressionRef("ExpNeg1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp((double) -1))));

        try {
            result = context.resolveExpressionRef("Exp1000").getExpression().evaluate(context);
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in positive infinity"));
        }

        try {
            result = context.resolveExpressionRef("Exp1000D").getExpression().evaluate(context);
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in positive infinity"));
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.HighBoundaryEvaluator#evaluate(Context)}
     */
    @Test
    public void testHighBoundary() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("HighBoundaryDec").getExpression().evaluate(context);
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58799999")), 0);

        result = context.resolveExpressionRef("HighBoundaryDate").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 12)));

        result = context.resolveExpressionRef("HighBoundaryDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 59, 59, 999)));

        result = context.resolveExpressionRef("HighBoundaryTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 59, 999)));

        result = context.resolveExpressionRef("HighBoundaryNull").getExpression().evaluate(context);
        Assert.assertNull(result);

        result = context.resolveExpressionRef("HighBoundaryNullPrecision").getExpression().evaluate(context);
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888999")), 0);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LogEvaluator#evaluate(Context)}
     */
    @Test
    public void testLog() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("LogNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Log1BaseNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Log1Base1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Log1Base2").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Log1Base100").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Log1Base100Long").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Log16Base2").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = context.resolveExpressionRef("LogD125Base2").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-3)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LnEvaluator#evaluate(Context)}
     */
    @Test
    public void testLn() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("LnNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        try {
            result = context.resolveExpressionRef("Ln0").getExpression().evaluate(context);
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in negative infinity"));
        }

        try {
            result = context.resolveExpressionRef("LnNeg0").getExpression().evaluate(context);
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in negative infinity"));
        }

        result = context.resolveExpressionRef("Ln1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = context.resolveExpressionRef("Ln1Long").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = context.resolveExpressionRef("LnNeg1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Ln1000").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(Value.verifyPrecision(new BigDecimal("6.90775527"), null)));

        result = context.resolveExpressionRef("Ln1000D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("6.90775527")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LowBoundaryEvaluator#evaluate(Context)}
     */
    @Test
    public void testLowBoundary() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("LowBoundaryDec").getExpression().evaluate(context);
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58700000")), 0);

        result = context.resolveExpressionRef("LowBoundaryDate").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 1)));

        result = context.resolveExpressionRef("LowBoundaryDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 0, 0, 0)));

        result = context.resolveExpressionRef("LowBoundaryTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 0, 0)));

        result = context.resolveExpressionRef("LowBoundaryNull").getExpression().evaluate(context);
        Assert.assertNull(result);

        result = context.resolveExpressionRef("LowBoundaryNullPrecision").getExpression().evaluate(context);
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888000")), 0);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MaxEvaluator#evaluate(Context)}
     */
    @Test
    public void testMaximum() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("IntegerMaxValue").getExpression().evaluate(context);
        assertThat(result, is(Integer.MAX_VALUE));

        result = context.resolveExpressionRef("LongMaxValue").getExpression().evaluate(context);
        assertThat(result, is(Long.MAX_VALUE));

        result = context.resolveExpressionRef("DecimalMaxValue").getExpression().evaluate(context);
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("9999999999999999999999999999.99999999")), 0);

        result = context.resolveExpressionRef("DateTimeMaxValue").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 9999, 12, 31, 23, 59, 59, 999)));

        result = context.resolveExpressionRef("TimeMaxValue").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MinEvaluator#evaluate(Context)}
     */
    @Test
    public void testMinimum() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("IntegerMinValue").getExpression().evaluate(context);
        assertThat(result, is(Integer.MIN_VALUE));

        result = context.resolveExpressionRef("LongMinValue").getExpression().evaluate(context);
        assertThat(result, is(Long.MIN_VALUE));

        result = context.resolveExpressionRef("DecimalMinValue").getExpression().evaluate(context);
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("-9999999999999999999999999999.99999999")) == 0);

        result = context.resolveExpressionRef("DateTimeMinValue").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1, 1, 1, 0, 0, 0, 0)));

        result = context.resolveExpressionRef("TimeMinValue").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ModuloEvaluator#evaluate(Context)}
     */
    @Test
    public void testModulo() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("ModuloNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Modulo0By0").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Modulo4By2").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("Modulo4By2Long").getExpression().evaluate(context);
        assertThat(result, is(0L));

        result = context.resolveExpressionRef("Modulo4DBy2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Modulo10By3").getExpression().evaluate(context);
        assertThat(result, is((1)));

        result = context.resolveExpressionRef("Modulo10DBy3D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("Modulo10IBy3D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("ModuloDResult").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.5)));

        result = context.resolveExpressionRef("Modulo10By3Quantity").getExpression().evaluate(context);
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1.0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("Modulo10By0Quantity").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MultiplyEvaluator#evaluate(Context)}
     */
    @Test
    public void testMultiply() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("MultiplyNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Multiply1By1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Multiply2By3Long").getExpression().evaluate(context);
        assertThat(result, is(6L));

        result = context.resolveExpressionRef("Multiply1DBy2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = context.resolveExpressionRef("Multiply1IBy2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        // TODO: should return multiplied units i.e. cm2
        // result = context.resolveExpressionRef("Multiply1CMBy2CM").getExpression().evaluate(context);
        // Assert.assertTrue(new BigDecimal("2.0").compareTo(((Quantity) result).getValue()) == 0);
        // Assert.assertEquals("cm", ((Quantity) result).getUnit());
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NegateEvaluator#evaluate(Context)}
     */
    @Test
    public void testNegate() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("NegateNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Negate0").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("NegateNeg0").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("Negate1").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("Negate1Long").getExpression().evaluate(context);
        assertThat(result, is(-1L));

        result = context.resolveExpressionRef("NegateMaxLong").getExpression().evaluate(context);
        assertThat(result, is(-9223372036854775808L));

        result = context.resolveExpressionRef("NegateNeg1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Negate0D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(0d))));

        result = context.resolveExpressionRef("NegateNeg0D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = context.resolveExpressionRef("Negate1D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double) -1)));

        result = context.resolveExpressionRef("NegateNeg1D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("Negate1CM").getExpression().evaluate(context);
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PredecessorEvaluator#evaluate(Context)}
     */
    @Test
    public void testPredecessor() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("PredecessorNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("PredecessorOf0").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("PredecessorOf1").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("PredecessorOf1Long").getExpression().evaluate(context);
        assertThat(result, is(0L));

        result = context.resolveExpressionRef("PredecessorOf1D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo((new BigDecimal("0.99999999"))));

        result = context.resolveExpressionRef("PredecessorOf101D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00999999")));

//        result = context.resolveExpressionRef("PredecessorOf1QCM").getExpression().evaluate(context);
//        Assert.assertTrue(new BigDecimal("0.99999999").compareTo(((Quantity) result).getValue()) == 0);
//        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = context.resolveExpressionRef("PredecessorOfJan12000").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1999, 12, 31)));

        result = context.resolveExpressionRef("PredecessorOfNoon").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(11, 59, 59, 999)));

        try {
            result = context.resolveExpressionRef("PredecessorUnderflowDt").getExpression().evaluate(context);
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The year: 0 falls below the accepted bounds of 0001-9999."));
        }

        try {
            result = context.resolveExpressionRef("PredecessorUnderflowT").getExpression().evaluate(context);
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The result of the successor operation precedes the minimum value allowed for the Time type"));
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PrecisionEvaluator#evaluate(Context)}
     */
    @Test
    public void testPrecision() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("PrecisionDecimal5").getExpression().evaluate(context);
        Assert.assertEquals(result, 5);

        result = context.resolveExpressionRef("PrecisionDateYear").getExpression().evaluate(context);
        Assert.assertEquals(result, 4);

        result = context.resolveExpressionRef("PrecisionDateTimeMs").getExpression().evaluate(context);
        Assert.assertEquals(result, 17);

        result = context.resolveExpressionRef("PrecisionTimeMinute").getExpression().evaluate(context);
        Assert.assertEquals(result, 4);

        result = context.resolveExpressionRef("PrecisionTimeMs").getExpression().evaluate(context);
        Assert.assertEquals(result, 9);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PowerEvaluator#evaluate(Context)}
     */
    @Test
    public void testPower() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("PowerNullToNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Power0To0").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Power2To2").getExpression().evaluate(context);
        assertThat(result, is(4));

        result = context.resolveExpressionRef("Power2To2Long").getExpression().evaluate(context);
        assertThat(result, is(4L));

        result = context.resolveExpressionRef("PowerNeg2To2").getExpression().evaluate(context);
        assertThat(result, is(4));

        result = context.resolveExpressionRef("Power2ToNeg2").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.25")));

        result = context.resolveExpressionRef("Power2DTo2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = context.resolveExpressionRef("PowerNeg2DTo2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = context.resolveExpressionRef("Power2DToNeg2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.25)));

        result = context.resolveExpressionRef("Power2DTo2").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = context.resolveExpressionRef("Power2To2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = context.resolveExpressionRef("Power2To4").getExpression().evaluate(context);
        assertThat(result, is(16));

        result = context.resolveExpressionRef("Power2To3Long").getExpression().evaluate(context);
        assertThat(result, is(8L));

        result = context.resolveExpressionRef("Power2DTo4D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("16.0")));

        result = context.resolveExpressionRef("Power2DToNeg2DEquivalence").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.RoundEvaluator#evaluate(Context)}
     */
    @Test
    public void testRound() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("RoundNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Round1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("Round0D5").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = context.resolveExpressionRef("Round0D4").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = context.resolveExpressionRef("Round3D14159").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.14")));

        result = context.resolveExpressionRef("RoundNeg0D5").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = context.resolveExpressionRef("RoundNeg0D4").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = context.resolveExpressionRef("RoundNeg0D6").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = context.resolveExpressionRef("RoundNeg1D1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = context.resolveExpressionRef("RoundNeg1D5").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = context.resolveExpressionRef("RoundNeg1D6").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2.0)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SubtractEvaluator#evaluate(Context)}
     */
    @Test
    public void testSubtract() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("SubtractNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Subtract1And1").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("Subtract1And1Long").getExpression().evaluate(context);
        assertThat(result, is(0L));

        result = context.resolveExpressionRef("Subtract1DAnd2D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = context.resolveExpressionRef("Subtract1CMAnd2CM").getExpression().evaluate(context);
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = context.resolveExpressionRef("Subtract2And11D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.9")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SuccessorEvaluator#evaluate(Context)}
     */
    @Test
    public void testSuccessor() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("SuccessorNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SuccessorOf0").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("SuccessorOf1").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("SuccessorOf1Long").getExpression().evaluate(context);
        assertThat(result, is(2L));

        result = context.resolveExpressionRef("SuccessorOf1D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00000001")));

        result = context.resolveExpressionRef("SuccessorOf101D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.01000001")));

        result = context.resolveExpressionRef("SuccessorOfJan12000").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 1, 2)));

        result = context.resolveExpressionRef("SuccessorOfNoon").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(12, 0, 0, 1)));

        try {
            result = context.resolveExpressionRef("SuccessorOverflowDt").getExpression().evaluate(context);
            Assert.fail();
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The year: 10000 falls above the accepted bounds of 0001-9999."));
        }

        try {
            result = context.resolveExpressionRef("SuccessorOverflowT").getExpression().evaluate(context);
            Assert.fail();
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The result of the successor operation exceeds the maximum value allowed for the Time type"));
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TruncateEvaluator#evaluate(Context)}
     */
    @Test
    public void testTruncate() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("TruncateNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Truncate0").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("Truncate0D0").getExpression().evaluate(context);
        assertThat(result, is((0)));

        result = context.resolveExpressionRef("Truncate0D1").getExpression().evaluate(context);
        assertThat(result, is((0)));

        result = context.resolveExpressionRef("Truncate1").getExpression().evaluate(context);
        assertThat(result, is((1)));

        result = context.resolveExpressionRef("Truncate1D0").getExpression().evaluate(context);
        assertThat(result, is((1)));

        result = context.resolveExpressionRef("Truncate1D1").getExpression().evaluate(context);
        assertThat(result, is((1)));

        result = context.resolveExpressionRef("Truncate1D9").getExpression().evaluate(context);
        assertThat(result, is((1)));

        result = context.resolveExpressionRef("TruncateNeg1").getExpression().evaluate(context);
        assertThat(result, is((-1)));

        result = context.resolveExpressionRef("TruncateNeg1D0").getExpression().evaluate(context);
        assertThat(result, is((-1)));

        result = context.resolveExpressionRef("TruncateNeg1D1").getExpression().evaluate(context);
        assertThat(result, is((-1)));

        result = context.resolveExpressionRef("TruncateNeg1D9").getExpression().evaluate(context);
        assertThat(result, is((-1)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TruncatedDivideEvaluator#evaluate(Context)}
     */
    @Test
    public void testTruncatedDivide() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("TruncatedDivideNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TruncatedDivide2By1").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("TruncatedDivide10By3").getExpression().evaluate(context);
        assertThat(result, is(3));

        result = context.resolveExpressionRef("TruncatedDivide10d1By3D1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = context.resolveExpressionRef("TruncatedDivideNeg2ByNeg1").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("TruncatedDivideNeg10ByNeg3").getExpression().evaluate(context);
        assertThat(result, is(3));

        result = context.resolveExpressionRef("TruncatedDivideNeg10d1ByNeg3D1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = context.resolveExpressionRef("TruncatedDivideNeg2By1").getExpression().evaluate(context);
        assertThat(result, is(-2));

        result = context.resolveExpressionRef("TruncatedDivideNeg10By3").getExpression().evaluate(context);
        assertThat(result, is(-3));

        result = context.resolveExpressionRef("TruncatedDivideNeg10d1By3D1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = context.resolveExpressionRef("TruncatedDivide2ByNeg1").getExpression().evaluate(context);
        assertThat(result, is((-2)));

        result = context.resolveExpressionRef("TruncatedDivide10ByNeg3").getExpression().evaluate(context);
        assertThat(result, is(-3));

        result = context.resolveExpressionRef("TruncatedDivide10d1ByNeg3D1").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = context.resolveExpressionRef("TruncatedDivide10By5D").getExpression().evaluate(context);
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("TruncatedDivide10By5DQuantity").getExpression().evaluate(context);
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = context.resolveExpressionRef("TruncatedDivide414By206DQuantity").getExpression().evaluate(context);
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("m"));

        result = context.resolveExpressionRef("TruncatedDivide10By0DQuantity").getExpression().evaluate(context);
        assertThat(result, nullValue());
    }
}
