package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.elm.executing.*;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.exception.UndefinedResult;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("removal")
public class CqlArithmeticFunctionsTest extends CqlTestBase {

    private static final VersionedIdentifier library = new VersionedIdentifier().withId("CqlArithmeticFunctionsTest");

  @Test
    public void testAbs() {

        Object result = engine.expression(library, "AbsNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Abs0").value();
        assertThat(result, is(0));

        result = engine.expression(library, "AbsNeg1").value();
        assertThat(result, is(1));

        result = engine.expression(library, "AbsNeg1Long").value();
        assertThat(result, is(1L));

        result = engine.expression(library, "AbsNeg1Dec").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "Abs0Dec").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.0)));

        result = engine.expression(library, "Abs1cm").value();
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

        Object result = engine.expression(library, "AddNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Add11").value();
        assertThat(result, is(2));

        result = engine.expression(library, "Add12Long").value();
        assertThat(result, is(3L));

        result = engine.expression(library, "Add1D1D").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = engine.expression(library, "Add1Q1Q").value();
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("2"));
        Assert.assertEquals(((Quantity) result).getUnit(), "g/cm3");

        result = engine.expression(library, "AddIAndD").value();
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

        Object result = engine.expression(library, "CeilingNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Ceiling1D").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Ceiling1D1").value();
        assertThat(result, is(2));

        result = engine.expression(library, "CeilingNegD1").value();
        assertThat(result, is(0));

        result = engine.expression(library, "CeilingNeg1").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "CeilingNeg1D1").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "Ceiling1I").value();
        assertThat(result, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DivideEvaluator#evaluate(Context)}
     */
    @Test
    public void testDivide() {

        Object result = engine.expression(library, "DivideNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Divide10").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Divide01").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.0")));

        result = engine.expression(library, "Divide11").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = engine.expression(library, "Divide11Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = engine.expression(library, "Divide1d1d").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = engine.expression(library, "Divide103").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.33333333")));

        result = engine.expression(library, "Divide1Q1").value();
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1"));
        Assert.assertEquals(((Quantity) result).getUnit(), "g/cm3");

        // TODO: The asserted "correct" answer 1.0'g/cm3' is wrong;
        // the true correct answer is just 1.0 with no units or empty string unit.
        // result = engine.expression(arithmetic, "Divide1Q1Q").value();
        // Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1.0"));
        // Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        result = engine.expression(library, "Divide10I5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = engine.expression(library, "Divide10I5I").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = engine.expression(library, "Divide10Q5I").value();
        Assert.assertEquals(new BigDecimal("2.0"), ((Quantity) result).getValue());
        Assert.assertEquals(((Quantity) result).getUnit(), "g");
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.FloorEvaluator#evaluate(Context)}
     */
    @Test
    public void testFloor() {

        Object result = engine.expression(library, "FloorNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Floor1").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Floor1D").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Floor1D1").value();
        assertThat(result, is(1));

        result = engine.expression(library, "FloorNegD1").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "FloorNeg1").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "FloorNeg1D1").value();
        assertThat(result, is(-2));

        result = engine.expression(library, "Floor2I").value();
        assertThat(result, is(2));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ExpEvaluator#evaluate(Context)}
     */
    @Test
    public void testExp() {

        Object result = engine.expression(library, "ExpNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Exp0").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "ExpNeg0").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "Exp1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(Math.exp(1d))));

        result = engine.expression(library, "Exp1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(Math.exp(1d))));

        result = engine.expression(library, "ExpNeg1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(Math.exp((double) -1))));

        try {
            result = engine.expression(library, "Exp1000").value();
            Assert.fail();
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in positive infinity"));
        }

        try {
            result = engine.expression(library, "Exp1000D").value();
            Assert.fail();
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in positive infinity"));
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.HighBoundaryEvaluator#evaluate(Context)}
     */
    @Test
    public void testHighBoundary() {

        Object result = engine.expression(library, "HighBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58799999")), 0);

        result = engine.expression(library, "HighBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 12)));

        result = engine.expression(library, "HighBoundaryDateTime").value();
        final DateTime expectedDateTime = new DateTime(getBigDecimalZoneOffset(), 2014, 1, 1, 8, 59, 59, 999);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTime));

        result = engine.expression(library, "HighBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 59, 999)));

        result = engine.expression(library, "HighBoundaryNull").value();
        Assert.assertNull(result);

        result = engine.expression(library, "HighBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888999")), 0);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LogEvaluator#evaluate(Context)}
     */
    @Test
    public void testLog() {

        Object result;

        result = engine.expression(library, "LogNullNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Log1BaseNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Log1Base1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Log1Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Log1Base100").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Log1Base100Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Log16Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(4d)));

        result = engine.expression(library, "LogD125Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf((double)-3)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LnEvaluator#evaluate(Context)}
     */
    @Test
    public void testLn() {

        Object result;

        result = engine.expression(library, "LnNull").value();
        assertThat(result, is(nullValue()));

        try {
            result = engine.expression(library, "Ln0").value();
            Assert.fail();
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in negative infinity"));
        }

        try {
            result = engine.expression(library, "LnNeg0").value();
            Assert.fail();
        } catch (UndefinedResult ae) {
            assertThat(ae.getMessage(), is("Results in negative infinity"));
        }

        result = engine.expression(library, "Ln1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0)));

        result = engine.expression(library, "Ln1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0)));

        result = engine.expression(library, "LnNeg1").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Ln1000").value();
        assertThat((BigDecimal)result, comparesEqualTo(Value.verifyPrecision(new BigDecimal("6.90775527"), null)));

        result = engine.expression(library, "Ln1000D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("6.90775527")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LowBoundaryEvaluator#evaluate(Context)}
     */
    @Test
    public void testLowBoundary() {

        Object result = engine.expression(library, "LowBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58700000")), 0);

        result = engine.expression(library, "LowBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 1)));

        result = engine.expression(library, "LowBoundaryDateTime").value();
        final DateTime expectedDateTime = new DateTime(getBigDecimalZoneOffset(), 2014, 1, 1, 8, 0, 0, 0);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTime));

        result = engine.expression(library, "LowBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 0, 0)));

        result = engine.expression(library, "LowBoundaryNull").value();
        Assert.assertNull(result);

        result = engine.expression(library, "LowBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888000")), 0);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MaxEvaluator#evaluate(Context)}
     */
    @Test
    public void testMaximum() {

        Object result = engine.expression(library, "IntegerMaxValue").value();
        assertThat(result, is(Integer.MAX_VALUE));

        result = engine.expression(library, "LongMaxValue").value();
        assertThat(result, is(Long.MAX_VALUE));

        result = engine.expression(library, "DecimalMaxValue").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("9999999999999999999999999999.99999999")), 0);

        result = engine.expression(library, "DateTimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 9999, 12, 31, 23, 59, 59, 999)));

        result = engine.expression(library, "TimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MinEvaluator#evaluate(Context)}
     */
    @Test
    public void testMinimum() {

        Object result = engine.expression(library, "IntegerMinValue").value();
        assertThat(result, is(Integer.MIN_VALUE));

        result = engine.expression(library, "LongMinValue").value();
        assertThat(result, is(Long.MIN_VALUE));

        result = engine.expression(library, "DecimalMinValue").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("-9999999999999999999999999999.99999999")) == 0);

        result = engine.expression(library, "DateTimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 1, 1, 1, 0, 0, 0, 0)));

        result = engine.expression(library, "TimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ModuloEvaluator#evaluate(Context)}
     */
    @Test
    public void testModulo() {

        Object result = engine.expression(library, "ModuloNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Modulo0By0").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Modulo4By2").value();
        assertThat(result, is(0));

        result = engine.expression(library, "Modulo4By2Long").value();
        assertThat(result, is(0L));

        result = engine.expression(library, "Modulo4DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Modulo10By3").value();
        assertThat(result, is((1)));

        result = engine.expression(library, "Modulo10DBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "Modulo10IBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "ModuloDResult").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.5)));

        result = engine.expression(library, "Modulo10By3Quantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(BigDecimal.valueOf(1.0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = engine.expression(library, "Modulo10By0Quantity").value();
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MultiplyEvaluator#evaluate(Context)}
     */
    @Test
    public void testMultiply() {

        Object result = engine.expression(library, "MultiplyNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Multiply1By1").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Multiply2By3Long").value();
        assertThat(result, is(6L));

        result = engine.expression(library, "Multiply1DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(2.0)));

        result = engine.expression(library, "Multiply1IBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(2.0)));

        // TODO: should return multiplied units i.e. cm2
        // result = engine.expression(arithmetic, "Multiply1CMBy2CM").value();
        // Assert.assertTrue(new BigDecimal("2.0").compareTo(((Quantity) result).getValue()) == 0);
        // Assert.assertEquals("cm", ((Quantity) result).getUnit());
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NegateEvaluator#evaluate(Context)}
     */
    @Test
    public void testNegate() {

        Object result;

        result = engine.expression(library, "NegateNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Negate0").value();
        assertThat(result, is(0));

        result = engine.expression(library, "NegateNeg0").value();
        assertThat(result, is(0));

        result = engine.expression(library, "Negate1").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "Negate1Long").value();
        assertThat(result, is(-1L));

        result = engine.expression(library, "NegateMaxLong").value();
        assertThat(result, is(-9223372036854775808L));

        result = engine.expression(library, "NegateNeg1").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Negate0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-(0d))));

        result = engine.expression(library, "NegateNeg0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0d)));

        result = engine.expression(library, "Negate1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf((double) -1)));

        result = engine.expression(library, "NegateNeg1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = engine.expression(library, "Negate1CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals(((Quantity) result).getUnit(), "cm");
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PredecessorEvaluator#evaluate(Context)}
     */
    @Test
    public void testPredecessor() {

        Object result;

        result = engine.expression(library, "PredecessorNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "PredecessorOf0").value();
        assertThat(result, is(-1));

        result = engine.expression(library, "PredecessorOf1").value();
        assertThat(result, is(0));

        result = engine.expression(library, "PredecessorOf1Long").value();
        assertThat(result, is(0L));

        result = engine.expression(library, "PredecessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo((new BigDecimal("0.99999999"))));

        result = engine.expression(library, "PredecessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00999999")));

//        result = engine.expression(arithmetic, "PredecessorOf1QCM").value();
//        Assert.assertTrue(new BigDecimal("0.99999999").compareTo(((Quantity) result).getValue()) == 0);
//        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = engine.expression(library, "PredecessorOfJan12000").value();
        final DateTime expectedDateTime = new DateTime(getBigDecimalZoneOffset(), 1999, 12, 31);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTime));

        result = engine.expression(library, "PredecessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(11, 59, 59, 999)));

        try {
            result = engine.expression(library, "PredecessorUnderflowDt").value();
            Assert.fail();
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The year: 0 falls below the accepted bounds of 0001-9999."));
        }

        try {
            result = engine.expression(library, "PredecessorUnderflowT").value();
            Assert.fail();
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The result of the successor operation precedes the minimum value allowed for the Time type"));
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PrecisionEvaluator#evaluate(Context)}
     */
    @Test
    public void testPrecision() {

        Object result = engine.expression(library, "PrecisionDecimal5").value();
        Assert.assertEquals(result, 5);

        result = engine.expression(library, "PrecisionDateYear").value();
        Assert.assertEquals(result, 4);

        result = engine.expression(library, "PrecisionDateTimeMs").value();
        Assert.assertEquals(result, 17);

        result = engine.expression(library, "PrecisionTimeMinute").value();
        Assert.assertEquals(result, 4);

        result = engine.expression(library, "PrecisionTimeMs").value();
        Assert.assertEquals(result, 9);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PowerEvaluator#evaluate(Context)}
     */
    @Test
    public void testPower() {

        Object result;

        result = engine.expression(library, "PowerNullToNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Power0To0").value();
        assertThat(result, is(1));

        result = engine.expression(library, "Power2To2").value();
        assertThat(result, is(4));

        result = engine.expression(library, "Power2To2Long").value();
        assertThat(result, is(4L));

        result = engine.expression(library, "PowerNeg2To2").value();
        assertThat(result, is(4));

        result = engine.expression(library, "Power2ToNeg2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.25")));

        result = engine.expression(library, "Power2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(4d)));

        result = engine.expression(library, "PowerNeg2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(4d)));

        result = engine.expression(library, "Power2DToNeg2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.25)));

        result = engine.expression(library, "Power2DTo2").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(4d)));

        result = engine.expression(library, "Power2To2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(4d)));

        result = engine.expression(library, "Power2To4").value();
        assertThat(result, is(16));

        result = engine.expression(library, "Power2To3Long").value();
        assertThat(result, is(8L));

        result = engine.expression(library, "Power2DTo4D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("16.0")));

        result = engine.expression(library, "Power2DToNeg2DEquivalence").value();
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.RoundEvaluator#evaluate(Context)}
     */
    @Test
    public void testRound() {

        Object result;

        result = engine.expression(library, "RoundNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Round1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "Round0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(1.0)));

        result = engine.expression(library, "Round0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.0)));

        result = engine.expression(library, "Round3D14159").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.14")));

        result = engine.expression(library, "RoundNeg0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.0)));

        result = engine.expression(library, "RoundNeg0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(0.0)));

        result = engine.expression(library, "RoundNeg0D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-1.0)));

        result = engine.expression(library, "RoundNeg1D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-1.0)));

        result = engine.expression(library, "RoundNeg1D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-1.0)));

        result = engine.expression(library, "RoundNeg1D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-2.0)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SubtractEvaluator#evaluate(Context)}
     */
    @Test
    public void testSubtract() {

        Object result;

        result = engine.expression(library, "SubtractNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Subtract1And1").value();
        assertThat(result, is(0));

        result = engine.expression(library, "Subtract1And1Long").value();
        assertThat(result, is(0L));

        result = engine.expression(library, "Subtract1DAnd2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-1.0)));

        result = engine.expression(library, "Subtract1CMAnd2CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals(((Quantity) result).getUnit(), "cm");

        result = engine.expression(library, "Subtract2And11D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.9")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SuccessorEvaluator#evaluate(Context)}
     */
    @Test
    public void testSuccessor() {

        Object result;

        result = engine.expression(library, "SuccessorNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "SuccessorOf0").value();
        assertThat(result, is(1));

        result = engine.expression(library, "SuccessorOf1").value();
        assertThat(result, is(2));

        result = engine.expression(library, "SuccessorOf1Long").value();
        assertThat(result, is(2L));

        result = engine.expression(library, "SuccessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00000001")));

        result = engine.expression(library, "SuccessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.01000001")));

        result = engine.expression(library, "SuccessorOfJan12000").value();
        final DateTime expectedDateTime = new DateTime(getBigDecimalZoneOffset(), 2000, 1, 2);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, expectedDateTime));

        result = engine.expression(library, "SuccessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(12, 0, 0, 1)));

        try {
            result = engine.expression(library, "SuccessorOverflowDt").value();
            Assert.fail();
        } catch (RuntimeException re) {
            assertThat(re.getMessage(), is("The year: 10000 falls above the accepted bounds of 0001-9999."));
        }

        try {
            result = engine.expression(library, "SuccessorOverflowT").value();
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

        Object result;

        result = engine.expression(library, "TruncateNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "Truncate0").value();
        assertThat(result, is(0));

        result = engine.expression(library, "Truncate0D0").value();
        assertThat(result, is((0)));

        result = engine.expression(library, "Truncate0D1").value();
        assertThat(result, is((0)));

        result = engine.expression(library, "Truncate1").value();
        assertThat(result, is((1)));

        result = engine.expression(library, "Truncate1D0").value();
        assertThat(result, is((1)));

        result = engine.expression(library, "Truncate1D1").value();
        assertThat(result, is((1)));

        result = engine.expression(library, "Truncate1D9").value();
        assertThat(result, is((1)));

        result = engine.expression(library, "TruncateNeg1").value();
        assertThat(result, is((-1)));

        result = engine.expression(library, "TruncateNeg1D0").value();
        assertThat(result, is((-1)));

        result = engine.expression(library, "TruncateNeg1D1").value();
        assertThat(result, is((-1)));

        result = engine.expression(library, "TruncateNeg1D9").value();
        assertThat(result, is((-1)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TruncatedDivideEvaluator#evaluate(Context)}
     */
    @Test
    public void testTruncatedDivide() {

        Object result;

        result = engine.expression(library, "TruncatedDivideNull").value();
        assertThat(result, is(nullValue()));

        result = engine.expression(library, "TruncatedDivide2By1").value();
        assertThat(result, is(2));

        result = engine.expression(library, "TruncatedDivide10By3").value();
        assertThat(result, is(3));

        result = engine.expression(library, "TruncatedDivide10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(3.0)));

        result = engine.expression(library, "TruncatedDivideNeg2ByNeg1").value();
        assertThat(result, is(2));

        result = engine.expression(library, "TruncatedDivideNeg10ByNeg3").value();
        assertThat(result, is(3));

        result = engine.expression(library, "TruncatedDivideNeg10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(3.0)));

        result = engine.expression(library, "TruncatedDivideNeg2By1").value();
        assertThat(result, is(-2));

        result = engine.expression(library, "TruncatedDivideNeg10By3").value();
        assertThat(result, is(-3));

        result = engine.expression(library, "TruncatedDivideNeg10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-3.0)));

        result = engine.expression(library, "TruncatedDivide2ByNeg1").value();
        assertThat(result, is((-2)));

        result = engine.expression(library, "TruncatedDivide10ByNeg3").value();
        assertThat(result, is(-3));

        result = engine.expression(library, "TruncatedDivide10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(BigDecimal.valueOf(-3.0)));

        result = engine.expression(library, "TruncatedDivide10By5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = engine.expression(library, "TruncatedDivide10By5DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = engine.expression(library, "TruncatedDivide414By206DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("m"));

        result = engine.expression(library, "TruncatedDivide10By0DQuantity").value();
        assertThat(result, nullValue());
    }
}
