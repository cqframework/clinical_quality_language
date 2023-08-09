package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.*;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CqlArithmeticFunctionsTest extends CqlTestBase {

    @Test
    public void test_all_arithmetic_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlArithmeticFunctionsTest"));

        assertThat(evaluationResult.forExpression("AbsNull").value(), is(nullValue()));
        assertThat(evaluationResult.forExpression("Abs0").value(), is(0));
        assertThat(evaluationResult.forExpression("AbsNeg1").value(), is(1));
        assertThat(evaluationResult.forExpression("AbsNeg1Long").value(), is(1L));
        assertThat((BigDecimal)evaluationResult.forExpression("AbsNeg1Dec").value(), comparesEqualTo(new BigDecimal(1.0)));
        assertThat((BigDecimal)evaluationResult.forExpression("Abs0Dec").value(), comparesEqualTo(new BigDecimal(0.0)));
        Object result = evaluationResult.forExpression("Abs1cm").value();
        Assert.assertTrue(((Quantity)result).compareTo(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")) == 0);

        try {
            result = AbsEvaluator.abs("This is an error");
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }

        assertThat(evaluationResult.forExpression("Add11").value(), is(2));
        assertThat(evaluationResult.forExpression("AddNull").value(), is(nullValue()));
        assertThat(evaluationResult.forExpression("Add12Long").value(), is(3L));
        assertThat(evaluationResult.forExpression("Add1D1D").value(), is(new BigDecimal("2.0")));
        assertThat(evaluationResult.forExpression("AddIAndD").value(), is(new BigDecimal("3.0")));
        result = evaluationResult.forExpression("Add1Q1Q").value();
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("2"));
        Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        // error testing
        try {
            result = AddEvaluator.add("This is an error", 404);
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }



        result = evaluationResult.forExpression("CeilingNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Ceiling1D").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Ceiling1D1").value();
        assertThat(result, is(2));

        assertThat(evaluationResult.forExpression("CeilingNegD1").value(), is(0));
        assertThat(evaluationResult.forExpression("CeilingNeg1").value(), is(-1));
        assertThat(evaluationResult.forExpression("CeilingNeg1D1").value(), is(-1));
        assertThat(evaluationResult.forExpression("Ceiling1I").value(), is(1));


        result = evaluationResult.forExpression("DivideNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.forExpression("Divide10").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.forExpression("Divide01").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("0.0")));


        result = evaluationResult.forExpression("Divide11").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("Divide11").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("Divide11Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("Divide1d1d").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("Divide103").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.33333333")));

        result = evaluationResult.forExpression("Divide1Q1").value();
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1"));
        Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        // TODO: The asserted "correct" answer 1.0'g/cm3' is wrong;
        // the true correct answer is just 1.0 with no units or empty string unit.
        // result = evaluationResult.forExpression("Divide1Q1Q").value();
        // Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1.0"));
        // Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        result = evaluationResult.forExpression("Divide10I5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("Divide10I5I").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("Divide10Q5I").value();
        Assert.assertEquals(new BigDecimal("2.0"), ((Quantity) result).getValue());
        Assert.assertEquals("g", ((Quantity) result).getUnit());




        result = evaluationResult.forExpression("FloorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Floor1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Floor1D").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Floor1D1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("FloorNegD1").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("FloorNeg1").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("FloorNeg1D1").value();
        assertThat(result, is(-2));

        result = evaluationResult.forExpression("Floor2I").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("ExpNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Exp0").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("ExpNeg0").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("Exp1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = evaluationResult.forExpression("Exp1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = evaluationResult.forExpression("ExpNeg1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp((double) -1))));

        result = evaluationResult.forExpression("HighBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58799999")), 0);

        result = evaluationResult.forExpression("HighBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 12)));

        result = evaluationResult.forExpression("HighBoundaryDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 59, 59, 999)));

        result = evaluationResult.forExpression("HighBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 59, 999)));

        result = evaluationResult.forExpression("HighBoundaryNull").value();
        Assert.assertNull(result);

        result = evaluationResult.forExpression("HighBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888999")), 0);


        result = evaluationResult.forExpression("LogNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Log1BaseNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Log1Base1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Log1Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Log1Base100").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Log1Base100Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Log16Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.forExpression("LogD125Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-3)));

        result = evaluationResult.forExpression("LnNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.forExpression("Ln1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = evaluationResult.forExpression("Ln1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = evaluationResult.forExpression("LnNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Ln1000").value();
        assertThat((BigDecimal)result, comparesEqualTo(Value.verifyPrecision(new BigDecimal("6.90775527"), null)));

        result = evaluationResult.forExpression("Ln1000D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("6.90775527")));

        result = evaluationResult.forExpression("LowBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58700000")), 0);

        result = evaluationResult.forExpression("LowBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 1)));

        result = evaluationResult.forExpression("LowBoundaryDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 0, 0, 0)));

        result = evaluationResult.forExpression("LowBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 0, 0)));

        result = evaluationResult.forExpression("LowBoundaryNull").value();
        Assert.assertNull(result);

        result = evaluationResult.forExpression("LowBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888000")), 0);

        result = evaluationResult.forExpression("IntegerMaxValue").value();
        assertThat(result, is(Integer.MAX_VALUE));

        result = evaluationResult.forExpression("LongMaxValue").value();
        assertThat(result, is(Long.MAX_VALUE));

        result = evaluationResult.forExpression("DecimalMaxValue").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("9999999999999999999999999999.99999999")), 0);

        result = evaluationResult.forExpression("DateTimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 9999, 12, 31, 23, 59, 59, 999)));

        result = evaluationResult.forExpression("TimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = evaluationResult.forExpression("IntegerMinValue").value();
        assertThat(result, is(Integer.MIN_VALUE));

        result = evaluationResult.forExpression("LongMinValue").value();
        assertThat(result, is(Long.MIN_VALUE));

        result = evaluationResult.forExpression("DecimalMinValue").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("-9999999999999999999999999999.99999999")) == 0);

        result = evaluationResult.forExpression("DateTimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 1, 1, 1, 0, 0, 0, 0)));

        result = evaluationResult.forExpression("TimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));

        result = evaluationResult.forExpression("ModuloNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Modulo0By0").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Modulo4By2").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("Modulo4By2Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.forExpression("Modulo4DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Modulo10By3").value();
        assertThat(result, is((1)));

        result = evaluationResult.forExpression("Modulo10DBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("Modulo10IBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("ModuloDResult").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.5)));

        result = evaluationResult.forExpression("Modulo10By3Quantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1.0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.forExpression("Modulo10By0Quantity").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("MultiplyNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Multiply1By1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Multiply2By3Long").value();
        assertThat(result, is(6L));

        result = evaluationResult.forExpression("Multiply1DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.forExpression("Multiply1IBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        // TODO: should return multiplied units i.e. cm2
        // result = evaluationResult.forExpression("Multiply1CMBy2CM").value();
        // Assert.assertTrue(new BigDecimal("2.0").compareTo(((Quantity) result).getValue()) == 0);
        // Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.forExpression("NegateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Negate0").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("NegateNeg0").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("Negate1").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("Negate1Long").value();
        assertThat(result, is(-1L));

        result = evaluationResult.forExpression("NegateMaxLong").value();
        assertThat(result, is(-9223372036854775808L));

        result = evaluationResult.forExpression("NegateNeg1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Negate0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(0d))));

        result = evaluationResult.forExpression("NegateNeg0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.forExpression("Negate1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double) -1)));

        result = evaluationResult.forExpression("NegateNeg1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("Negate1CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.forExpression("PredecessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("PredecessorOf0").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("PredecessorOf1").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("PredecessorOf1Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.forExpression("PredecessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo((new BigDecimal("0.99999999"))));

        result = evaluationResult.forExpression("PredecessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00999999")));

//        result = evaluationResult.forExpression("PredecessorOf1QCM").value();
//        Assert.assertTrue(new BigDecimal("0.99999999").compareTo(((Quantity) result).getValue()) == 0);
//        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.forExpression("PredecessorOfJan12000").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1999, 12, 31)));

        result = evaluationResult.forExpression("PredecessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(11, 59, 59, 999)));

        result = evaluationResult.forExpression("PrecisionDecimal5").value();
        Assert.assertEquals(result, 5);

        result = evaluationResult.forExpression("PrecisionDateYear").value();
        Assert.assertEquals(result, 4);

        result = evaluationResult.forExpression("PrecisionDateTimeMs").value();
        Assert.assertEquals(result, 17);

        result = evaluationResult.forExpression("PrecisionTimeMinute").value();
        Assert.assertEquals(result, 4);

        result = evaluationResult.forExpression("PrecisionTimeMs").value();
        Assert.assertEquals(result, 9);

        result = evaluationResult.forExpression("PowerNullToNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Power0To0").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Power2To2").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("Power2To2Long").value();
        assertThat(result, is(4L));

        result = evaluationResult.forExpression("PowerNeg2To2").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("Power2ToNeg2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.25")));

        result = evaluationResult.forExpression("Power2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.forExpression("PowerNeg2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.forExpression("Power2DToNeg2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.25)));

        result = evaluationResult.forExpression("Power2DTo2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.forExpression("Power2To2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.forExpression("Power2To4").value();
        assertThat(result, is(16));

        result = evaluationResult.forExpression("Power2To3Long").value();
        assertThat(result, is(8L));

        result = evaluationResult.forExpression("Power2DTo4D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("16.0")));

        result = evaluationResult.forExpression("Power2DToNeg2DEquivalence").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("RoundNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Round1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("Round0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.forExpression("Round0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.forExpression("Round3D14159").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.14")));

        result = evaluationResult.forExpression("RoundNeg0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.forExpression("RoundNeg0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.forExpression("RoundNeg0D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.forExpression("RoundNeg1D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.forExpression("RoundNeg1D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.forExpression("RoundNeg1D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2.0)));

        result = evaluationResult.forExpression("SubtractNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Subtract1And1").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("Subtract1And1Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.forExpression("Subtract1DAnd2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.forExpression("Subtract1CMAnd2CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.forExpression("Subtract2And11D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.9")));

        result = evaluationResult.forExpression("SuccessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SuccessorOf0").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("SuccessorOf1").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("SuccessorOf1Long").value();
        assertThat(result, is(2L));

        result = evaluationResult.forExpression("SuccessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00000001")));

        result = evaluationResult.forExpression("SuccessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.01000001")));

        result = evaluationResult.forExpression("SuccessorOfJan12000").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 1, 2)));

        result = evaluationResult.forExpression("SuccessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(12, 0, 0, 1)));

        result = evaluationResult.forExpression("TruncateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Truncate0").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("Truncate0D0").value();
        assertThat(result, is((0)));

        result = evaluationResult.forExpression("Truncate0D1").value();
        assertThat(result, is((0)));

        result = evaluationResult.forExpression("Truncate1").value();
        assertThat(result, is((1)));

        result = evaluationResult.forExpression("Truncate1D0").value();
        assertThat(result, is((1)));

        result = evaluationResult.forExpression("Truncate1D1").value();
        assertThat(result, is((1)));

        result = evaluationResult.forExpression("Truncate1D9").value();
        assertThat(result, is((1)));

        result = evaluationResult.forExpression("TruncateNeg1").value();
        assertThat(result, is((-1)));

        result = evaluationResult.forExpression("TruncateNeg1D0").value();
        assertThat(result, is((-1)));

        result = evaluationResult.forExpression("TruncateNeg1D1").value();
        assertThat(result, is((-1)));

        result = evaluationResult.forExpression("TruncateNeg1D9").value();
        assertThat(result, is((-1)));

        result = evaluationResult.forExpression("TruncatedDivideNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TruncatedDivide2By1").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("TruncatedDivide10By3").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("TruncatedDivide10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = evaluationResult.forExpression("TruncatedDivideNeg2ByNeg1").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("TruncatedDivideNeg10ByNeg3").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("TruncatedDivideNeg10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = evaluationResult.forExpression("TruncatedDivideNeg2By1").value();
        assertThat(result, is(-2));

        result = evaluationResult.forExpression("TruncatedDivideNeg10By3").value();
        assertThat(result, is(-3));

        result = evaluationResult.forExpression("TruncatedDivideNeg10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = evaluationResult.forExpression("TruncatedDivide2ByNeg1").value();
        assertThat(result, is((-2)));

        result = evaluationResult.forExpression("TruncatedDivide10ByNeg3").value();
        assertThat(result, is(-3));

        result = evaluationResult.forExpression("TruncatedDivide10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = evaluationResult.forExpression("TruncatedDivide10By5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("TruncatedDivide10By5DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.forExpression("TruncatedDivide414By206DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("m"));

        result = evaluationResult.forExpression("TruncatedDivide10By0DQuantity").value();
        assertThat(result, nullValue());

    }
}
