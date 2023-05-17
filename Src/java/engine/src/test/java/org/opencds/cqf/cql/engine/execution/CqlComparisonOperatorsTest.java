package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.GreaterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlComparisonOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_comparison_operators_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlComparisonOperatorsTest"), null, null, null, null, null);


        Object result = evaluationResult.expressionResults.get("BetweenIntTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleEqTrueNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleEqNullTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleEqInt1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqInt1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqInt1Int2Long").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqStringAStringA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqStringAStringB").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Float1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Float1WithZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Float1WithPrecisionAndZ").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Float2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqFloat1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityEqCM1CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityEqDiffPrecision").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioEqual").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioNotEqual").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.expressionResults.get("QuantityEqCM1M01").value();
        // assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TupleEqJohnJohn").value();
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
//        result = evaluationResult.expressionResults.get("TupleEqJohnJohnFalse").value();
//        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
//        result = evaluationResult.expressionResults.get("TupleEqJohnJohnFalse2").value();
//        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleEqJohnJane").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleEqJohn1John2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleEqDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TupleEqDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleEqTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TupleEqTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeEqTodayToday").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeEqJanJan").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeEqJanJuly").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeEqNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeUTC").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeEqTodayYesterday").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeEq10A10A").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeEq10A10P").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterLong").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterDecZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterDecZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterDecZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterCM0CM0").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterCM0CM1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterCM0NegCM1").value();
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        //result = evaluationResult.expressionResults.get("GreaterM1CM1").value();
        //assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        //result = evaluationResult.expressionResults.get("GreaterM1CM10").value();
        //assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterAA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterAB").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterBA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterAThanAA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterAAThanA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterJackJill").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeGreaterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeGreaterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterFalse").value();
        assertThat(result, is(false));

        try {
            GreaterEvaluator.greater(1, "one", engineVisitor.getState());
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }

        result = evaluationResult.expressionResults.get("GreaterOrEqualZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualZ1Long").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualDecZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualDecZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualDecZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualCM0CM0").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualCM0CM1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualCM0NegCM1").value();
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.expressionResults.get("GreaterOrEqualM1CM1").value();
        // assertThat(result, is(true));
        //
        // result = evaluationResult.expressionResults.get("GreaterOrEqualM1CM10").value();
        // assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualAA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualAB").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualBA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualAThanAA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("GreaterOrEqualAAThanA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualJackJill").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeGreaterEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeGreaterEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeGreaterEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeGreaterEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeGreaterEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeGreaterEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("UncertaintyGreaterEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessLong").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessLongNeg").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessDecZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessDecZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessDecZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessCM0CM0").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessCM0CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessCM0NegCM1").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.expressionResults.get("LessM1CM1").value();
        // assertThat(result, is(false));

        // result = evaluationResult.expressionResults.get("LessM1CM10").value();
        // assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessAA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessAB").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessBA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessAThanAA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessAAThanA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessJackJill").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("UncertaintyLessNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("UncertaintyLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("UncertaintyLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualZ1Long").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualDecZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualDecZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualDecZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualCM0CM0").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualCM0CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualCM0NegCM1").value();
        assertThat(result, is(false));

        // TODO: uncomment once quantity unit comparison is implemented
        // result = evaluationResult.expressionResults.get("LessOrEqualM1CM1").value();
        // assertThat(result, is(false));
        //
        // result = evaluationResult.expressionResults.get("LessOrEqualM1CM10").value();
        // assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualAA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualAB").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualBA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualAThanAA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("LessOrEqualAAThanA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("LessOrEqualJackJill").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeLessEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeLessEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeLessEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeLessEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeLessEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeLessEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("UncertaintyLessEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("UncertaintyLessEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("UncertaintyLessEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivNullNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivTrueNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivNullTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivInt1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivInt1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivStringAStringA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivStringAStringB").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivStringIgnoreCase").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Float1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Float2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivFloat1Float1WithZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Float1WithPrecision").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Float1WithPrecisionAndZ").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloatTrailingZero").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFloat1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivEqCM1CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioEquivalent").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioNotEquivalent").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        //result = evaluationResult.expressionResults.get("EquivEqCM1M01").value();
        //assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivTupleJohnJohn").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivTupleJohnJohnWithNulls").value();
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
//        result = evaluationResult.expressionResults.get("EquivTupleJohnJohnFalse").value();
//        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
//        result = evaluationResult.expressionResults.get("EquivTupleJohnJohnFalse2").value();
//        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivTupleJohnJane").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivTupleJohn1John2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivDateTimeTodayToday").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivDateTimeTodayYesterday").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivTime10A10A").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivTime10A10P").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqTrueTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqTrueFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqFalseFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqFalseTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleNotEqTrueNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleNotEqNullTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SimpleNotEqInt1Int1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqInt1Int2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqInt1Int2Long").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqStringAStringA").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqStringAStringB").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqFloat1Float1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqFloat1Float2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqFloat1Int1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SimpleNotEqFloat1Int2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityNotEqCM1CM1").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        //result = evaluationResult.expressionResults.get("QuantityNotEqCM1M01").value();
        //assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleNotEqJohnJohn").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TupleNotEqJohnJane").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TupleNotEqJohn1John2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeNotEqTodayToday").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeNotEqTodayYesterday").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeNotEq10A10A").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeNotEq10A10P").value();
        assertThat(result, is(true));

    }
}
