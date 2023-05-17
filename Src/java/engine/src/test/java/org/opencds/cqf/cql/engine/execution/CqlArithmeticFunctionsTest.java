package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.*;
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

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlArithmeticFunctionsTest"), null, null, null, null, null);

        assertThat(evaluationResult.expressionResults.get("AbsNull").value(), is(nullValue()));
        assertThat(evaluationResult.expressionResults.get("Abs0").value(), is(0));
        assertThat(evaluationResult.expressionResults.get("AbsNeg1").value(), is(1));
        assertThat(evaluationResult.expressionResults.get("AbsNeg1Long").value(), is(1L));
        assertThat((BigDecimal)evaluationResult.expressionResults.get("AbsNeg1Dec").value(), comparesEqualTo(new BigDecimal(1.0)));
        assertThat((BigDecimal)evaluationResult.expressionResults.get("Abs0Dec").value(), comparesEqualTo(new BigDecimal(0.0)));
        Object result = evaluationResult.expressionResults.get("Abs1cm").value();
        Assert.assertTrue(((Quantity)result).compareTo(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")) == 0);

        try {
            result = AbsEvaluator.abs("This is an error");
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }

        assertThat(evaluationResult.expressionResults.get("Add11").value(), is(2));
        assertThat(evaluationResult.expressionResults.get("AddNull").value(), is(nullValue()));
        assertThat(evaluationResult.expressionResults.get("Add12Long").value(), is(3L));
        assertThat(evaluationResult.expressionResults.get("Add1D1D").value(), is(new BigDecimal("2.0")));
        assertThat(evaluationResult.expressionResults.get("AddIAndD").value(), is(new BigDecimal("3.0")));
        result = evaluationResult.expressionResults.get("Add1Q1Q").value();
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



        result = evaluationResult.expressionResults.get("CeilingNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Ceiling1D").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Ceiling1D1").value();
        assertThat(result, is(2));

        assertThat(evaluationResult.expressionResults.get("CeilingNegD1").value(), is(0));
        assertThat(evaluationResult.expressionResults.get("CeilingNeg1").value(), is(-1));
        assertThat(evaluationResult.expressionResults.get("CeilingNeg1D1").value(), is(-1));
        assertThat(evaluationResult.expressionResults.get("Ceiling1I").value(), is(1));


        result = evaluationResult.expressionResults.get("DivideNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Divide10").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Divide01").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("0.0")));


        result = evaluationResult.expressionResults.get("Divide11").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("Divide11").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("Divide11Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("Divide1d1d").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("Divide103").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.33333333")));

        result = evaluationResult.expressionResults.get("Divide1Q1").value();
        Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1"));
        Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        // TODO: The asserted "correct" answer 1.0'g/cm3' is wrong;
        // the true correct answer is just 1.0 with no units or empty string unit.
        // result = evaluationResult.expressionResults.get("Divide1Q1Q").value();
        // Assert.assertEquals(((Quantity) result).getValue(), new BigDecimal("1.0"));
        // Assert.assertEquals("g/cm3", ((Quantity) result).getUnit());

        result = evaluationResult.expressionResults.get("Divide10I5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("Divide10I5I").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("Divide10Q5I").value();
        Assert.assertEquals(new BigDecimal("2.0"), ((Quantity) result).getValue());
        Assert.assertEquals("g", ((Quantity) result).getUnit());




        result = evaluationResult.expressionResults.get("FloorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Floor1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Floor1D").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Floor1D1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("FloorNegD1").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("FloorNeg1").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("FloorNeg1D1").value();
        assertThat(result, is(-2));

        result = evaluationResult.expressionResults.get("Floor2I").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("ExpNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Exp0").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("ExpNeg0").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Exp1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = evaluationResult.expressionResults.get("Exp1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp(1d))));

        result = evaluationResult.expressionResults.get("ExpNeg1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(Math.exp((double) -1))));

        result = evaluationResult.expressionResults.get("HighBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58799999")), 0);

        result = evaluationResult.expressionResults.get("HighBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 12)));

        result = evaluationResult.expressionResults.get("HighBoundaryDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("HighBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 59, 999)));

        result = evaluationResult.expressionResults.get("HighBoundaryNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("HighBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888999")), 0);


        result = evaluationResult.expressionResults.get("LogNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Log1BaseNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Log1Base1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Log1Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Log1Base100").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Log1Base100Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Log16Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.expressionResults.get("LogD125Base2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double)-3)));

        result = evaluationResult.expressionResults.get("LnNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Ln1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = evaluationResult.expressionResults.get("Ln1Long").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0)));

        result = evaluationResult.expressionResults.get("LnNeg1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Ln1000").value();
        assertThat((BigDecimal)result, comparesEqualTo(Value.verifyPrecision(new BigDecimal("6.90775527"), null)));

        result = evaluationResult.expressionResults.get("Ln1000D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("6.90775527")));

        result = evaluationResult.expressionResults.get("LowBoundaryDec").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58700000")), 0);

        result = evaluationResult.expressionResults.get("LowBoundaryDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 1)));

        result = evaluationResult.expressionResults.get("LowBoundaryDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 8, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("LowBoundaryTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 30, 0, 0)));

        result = evaluationResult.expressionResults.get("LowBoundaryNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("LowBoundaryNullPrecision").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("1.58888000")), 0);

        result = evaluationResult.expressionResults.get("IntegerMaxValue").value();
        assertThat(result, is(Integer.MAX_VALUE));

        result = evaluationResult.expressionResults.get("LongMaxValue").value();
        assertThat(result, is(Long.MAX_VALUE));

        result = evaluationResult.expressionResults.get("DecimalMaxValue").value();
        Assert.assertEquals(((BigDecimal) result).compareTo(new BigDecimal("9999999999999999999999999999.99999999")), 0);

        result = evaluationResult.expressionResults.get("DateTimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 9999, 12, 31, 23, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeMaxValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("IntegerMinValue").value();
        assertThat(result, is(Integer.MIN_VALUE));

        result = evaluationResult.expressionResults.get("LongMinValue").value();
        assertThat(result, is(Long.MIN_VALUE));

        result = evaluationResult.expressionResults.get("DecimalMinValue").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("-9999999999999999999999999999.99999999")) == 0);

        result = evaluationResult.expressionResults.get("DateTimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(BigDecimal.ZERO, 1, 1, 1, 0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TimeMinValue").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("ModuloNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Modulo0By0").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Modulo4By2").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Modulo4By2Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.expressionResults.get("Modulo4DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Modulo10By3").value();
        assertThat(result, is((1)));

        result = evaluationResult.expressionResults.get("Modulo10DBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Modulo10IBy3D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("ModuloDResult").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.5)));

        result = evaluationResult.expressionResults.get("Modulo10By3Quantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal(1.0)));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("Modulo10By0Quantity").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("MultiplyNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Multiply1By1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Multiply2By3Long").value();
        assertThat(result, is(6L));

        result = evaluationResult.expressionResults.get("Multiply1DBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        result = evaluationResult.expressionResults.get("Multiply1IBy2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(2.0)));

        // TODO: should return multiplied units i.e. cm2
        // result = evaluationResult.expressionResults.get("Multiply1CMBy2CM").value();
        // Assert.assertTrue(new BigDecimal("2.0").compareTo(((Quantity) result).getValue()) == 0);
        // Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.expressionResults.get("NegateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Negate0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("NegateNeg0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Negate1").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("Negate1Long").value();
        assertThat(result, is(-1L));

        result = evaluationResult.expressionResults.get("NegateMaxLong").value();
        assertThat(result, is(-9223372036854775808L));

        result = evaluationResult.expressionResults.get("NegateNeg1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Negate0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-(0d))));

        result = evaluationResult.expressionResults.get("NegateNeg0D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("Negate1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal((double) -1)));

        result = evaluationResult.expressionResults.get("NegateNeg1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("Negate1CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.expressionResults.get("PredecessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("PredecessorOf0").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("PredecessorOf1").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("PredecessorOf1Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.expressionResults.get("PredecessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo((new BigDecimal("0.99999999"))));

        result = evaluationResult.expressionResults.get("PredecessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00999999")));

//        result = evaluationResult.expressionResults.get("PredecessorOf1QCM").value();
//        Assert.assertTrue(new BigDecimal("0.99999999").compareTo(((Quantity) result).getValue()) == 0);
//        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.expressionResults.get("PredecessorOfJan12000").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1999, 12, 31)));

        result = evaluationResult.expressionResults.get("PredecessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(11, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("PrecisionDecimal5").value();
        Assert.assertEquals(result, 5);

        result = evaluationResult.expressionResults.get("PrecisionDateYear").value();
        Assert.assertEquals(result, 4);

        result = evaluationResult.expressionResults.get("PrecisionDateTimeMs").value();
        Assert.assertEquals(result, 17);

        result = evaluationResult.expressionResults.get("PrecisionTimeMinute").value();
        Assert.assertEquals(result, 4);

        result = evaluationResult.expressionResults.get("PrecisionTimeMs").value();
        Assert.assertEquals(result, 9);

        result = evaluationResult.expressionResults.get("PowerNullToNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Power0To0").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Power2To2").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("Power2To2Long").value();
        assertThat(result, is(4L));

        result = evaluationResult.expressionResults.get("PowerNeg2To2").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("Power2ToNeg2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.25")));

        result = evaluationResult.expressionResults.get("Power2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.expressionResults.get("PowerNeg2DTo2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.expressionResults.get("Power2DToNeg2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.25)));

        result = evaluationResult.expressionResults.get("Power2DTo2").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.expressionResults.get("Power2To2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(4d)));

        result = evaluationResult.expressionResults.get("Power2To4").value();
        assertThat(result, is(16));

        result = evaluationResult.expressionResults.get("Power2To3Long").value();
        assertThat(result, is(8L));

        result = evaluationResult.expressionResults.get("Power2DTo4D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("16.0")));

        result = evaluationResult.expressionResults.get("Power2DToNeg2DEquivalence").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RoundNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Round1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Round0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Round0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.expressionResults.get("Round3D14159").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("3.14")));

        result = evaluationResult.expressionResults.get("RoundNeg0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.expressionResults.get("RoundNeg0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));

        result = evaluationResult.expressionResults.get("RoundNeg0D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.expressionResults.get("RoundNeg1D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.expressionResults.get("RoundNeg1D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.expressionResults.get("RoundNeg1D6").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-2.0)));

        result = evaluationResult.expressionResults.get("SubtractNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Subtract1And1").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Subtract1And1Long").value();
        assertThat(result, is(0L));

        result = evaluationResult.expressionResults.get("Subtract1DAnd2D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-1.0)));

        result = evaluationResult.expressionResults.get("Subtract1CMAnd2CM").value();
        Assert.assertTrue(new BigDecimal("-1.0").compareTo(((Quantity) result).getValue()) == 0);
        Assert.assertEquals("cm", ((Quantity) result).getUnit());

        result = evaluationResult.expressionResults.get("Subtract2And11D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("0.9")));

        result = evaluationResult.expressionResults.get("SuccessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SuccessorOf0").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("SuccessorOf1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("SuccessorOf1Long").value();
        assertThat(result, is(2L));

        result = evaluationResult.expressionResults.get("SuccessorOf1D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.00000001")));

        result = evaluationResult.expressionResults.get("SuccessorOf101D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("1.01000001")));

        result = evaluationResult.expressionResults.get("SuccessorOfJan12000").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 1, 2)));

        result = evaluationResult.expressionResults.get("SuccessorOfNoon").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(12, 0, 0, 1)));

        result = evaluationResult.expressionResults.get("TruncateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Truncate0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Truncate0D0").value();
        assertThat(result, is((0)));

        result = evaluationResult.expressionResults.get("Truncate0D1").value();
        assertThat(result, is((0)));

        result = evaluationResult.expressionResults.get("Truncate1").value();
        assertThat(result, is((1)));

        result = evaluationResult.expressionResults.get("Truncate1D0").value();
        assertThat(result, is((1)));

        result = evaluationResult.expressionResults.get("Truncate1D1").value();
        assertThat(result, is((1)));

        result = evaluationResult.expressionResults.get("Truncate1D9").value();
        assertThat(result, is((1)));

        result = evaluationResult.expressionResults.get("TruncateNeg1").value();
        assertThat(result, is((-1)));

        result = evaluationResult.expressionResults.get("TruncateNeg1D0").value();
        assertThat(result, is((-1)));

        result = evaluationResult.expressionResults.get("TruncateNeg1D1").value();
        assertThat(result, is((-1)));

        result = evaluationResult.expressionResults.get("TruncateNeg1D9").value();
        assertThat(result, is((-1)));

        result = evaluationResult.expressionResults.get("TruncatedDivideNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TruncatedDivide2By1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TruncatedDivide10By3").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("TruncatedDivide10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg2ByNeg1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg10ByNeg3").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg2By1").value();
        assertThat(result, is(-2));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg10By3").value();
        assertThat(result, is(-3));

        result = evaluationResult.expressionResults.get("TruncatedDivideNeg10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = evaluationResult.expressionResults.get("TruncatedDivide2ByNeg1").value();
        assertThat(result, is((-2)));

        result = evaluationResult.expressionResults.get("TruncatedDivide10ByNeg3").value();
        assertThat(result, is(-3));

        result = evaluationResult.expressionResults.get("TruncatedDivide10d1ByNeg3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(-3.0)));

        result = evaluationResult.expressionResults.get("TruncatedDivide10By5D").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("TruncatedDivide10By5DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("g"));

        result = evaluationResult.expressionResults.get("TruncatedDivide414By206DQuantity").value();
        assertThat(((Quantity)result).getValue(), comparesEqualTo(new BigDecimal("2.0")));
        assertThat(((Quantity)result).getUnit(), is("m"));

        result = evaluationResult.expressionResults.get("TruncatedDivide10By0DQuantity").value();
        assertThat(result, nullValue());

    }
}
