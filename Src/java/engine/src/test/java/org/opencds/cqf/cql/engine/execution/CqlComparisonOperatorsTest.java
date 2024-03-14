package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlComparisonOperatorsTest extends CqlTestBase {

    @Test
    public void test_cql_comparison_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlComparisonOperatorsTest"), errors, testCompilerOptions());
        assertFalse(
                CqlCompilerException.hasErrors(errors),
                String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    public void test_all_comparison_operators_tests() {
        var eng = getEngine(testCompilerOptions());
        var evaluationResult = eng.evaluate(toElmIdentifier("CqlComparisonOperatorsTest"));

        Object result = evaluationResult.forExpression("BetweenIntTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleEqTrueNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleEqNullTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleEqInt1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqInt1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqInt1Int2Long").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqStringAStringA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqStringAStringB").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqFloat1Float1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqFloat1Float1WithZ").value();
        assertThat(result, is(true));

        result = evaluationResult
                .forExpression("SimpleEqFloat1Float1WithPrecisionAndZ")
                .value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqFloat1Float2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleEqFloat1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleEqFloat1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityEqCM1CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityEqDiffPrecision").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("RatioEqual").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("RatioNotEqual").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("QuantityEqCM1M01").value();
        // assertThat(result, is(true));

        result = evaluationResult.forExpression("TupleEqJohnJohn").value();
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        result = evaluationResult.forExpression("TupleEqJohnJohnFalse").value();
        //        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        result = evaluationResult.forExpression("TupleEqJohnJohnFalse2").value();
        //        assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleEqJohnJane").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleEqJohn1John2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleEqDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TupleEqDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleEqTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TupleEqTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeEqTodayToday").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeEqJanJan").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeEqJanJuly").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeEqNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeUTC").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeEqTodayYesterday").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeEq10A10A").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeEq10A10P").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterLong").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterDecZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterDecZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterDecZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterCM0CM0").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterCM0CM1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterCM0NegCM1").value();
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("GreaterM1CM1").value();
        // assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("GreaterM1CM10").value();
        // assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterAA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterAB").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterBA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterAThanAA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterAAThanA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterJackJill").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeGreaterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeGreaterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("UncertaintyGreaterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("UncertaintyGreaterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("UncertaintyGreaterFalse").value();
        assertThat(result, is(false));

        try {
            GreaterEvaluator.greater(1, "one", engine.getState());
            Assert.fail();
        } catch (CqlException e) {
            // pass
        }

        result = evaluationResult.forExpression("GreaterOrEqualZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualZ1Long").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualDecZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualDecZ1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualDecZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualCM0CM0").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualCM0CM1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualCM0NegCM1").value();
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("GreaterOrEqualM1CM1").value();
        // assertThat(result, is(true));
        //
        // result = evaluationResult.forExpression("GreaterOrEqualM1CM10").value();
        // assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualAA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualAB").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualBA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualAThanAA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("GreaterOrEqualAAThanA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("GreaterOrEqualJackJill").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeGreaterEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeGreaterEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeGreaterEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeGreaterEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeGreaterEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeGreaterEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("UncertaintyGreaterEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("UncertaintyGreaterEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("UncertaintyGreaterEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessLong").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessLongNeg").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessDecZZ").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessDecZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessDecZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessCM0CM0").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessCM0CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessCM0NegCM1").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("LessM1CM1").value();
        // assertThat(result, is(false));

        // result = evaluationResult.forExpression("LessM1CM10").value();
        // assertThat(result, is(false));

        result = evaluationResult.forExpression("LessAA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessAB").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessBA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessAThanAA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessAAThanA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessJackJill").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("UncertaintyLessNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("UncertaintyLessTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("UncertaintyLessFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualZ1Long").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualDecZZ").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualDecZ1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualDecZNeg1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualCM0CM0").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualCM0CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualCM0NegCM1").value();
        assertThat(result, is(false));

        // TODO: uncomment once quantity unit comparison is implemented
        // result = evaluationResult.forExpression("LessOrEqualM1CM1").value();
        // assertThat(result, is(false));
        //
        // result = evaluationResult.forExpression("LessOrEqualM1CM10").value();
        // assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualAA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualAB").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualBA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualAThanAA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("LessOrEqualAAThanA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("LessOrEqualJackJill").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeLessEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeLessEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeLessEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeLessEqTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeLessEqTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeLessEqFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("UncertaintyLessEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("UncertaintyLessEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("UncertaintyLessEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivNullNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivTrueNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivNullTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivInt1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivInt1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivStringAStringA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivStringAStringB").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivStringIgnoreCase").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFloat1Float1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFloat1Float2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivFloat1Float1WithZ").value();
        assertThat(result, is(true));

        result =
                evaluationResult.forExpression("EquivFloat1Float1WithPrecision").value();
        assertThat(result, is(true));

        result = evaluationResult
                .forExpression("EquivFloat1Float1WithPrecisionAndZ")
                .value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFloatTrailingZero").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFloat1Int1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivFloat1Int2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivEqCM1CM1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("RatioEquivalent").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("RatioNotEquivalent").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("EquivEqCM1M01").value();
        // assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivTupleJohnJohn").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivTupleJohnJohnWithNulls").value();
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        result = evaluationResult.forExpression("EquivTupleJohnJohnFalse").value();
        //        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        result = evaluationResult.forExpression("EquivTupleJohnJohnFalse2").value();
        //        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivTupleJohnJane").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivTupleJohn1John2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivDateTimeTodayToday").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivDateTimeTodayYesterday").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivTime10A10A").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivTime10A10P").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqTrueTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqTrueFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqFalseFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqFalseTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleNotEqTrueNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleNotEqNullTrue").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SimpleNotEqInt1Int1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqInt1Int2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqInt1Int2Long").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqStringAStringA").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqStringAStringB").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqFloat1Float1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqFloat1Float2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("SimpleNotEqFloat1Int1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SimpleNotEqFloat1Int2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityNotEqCM1CM1").value();
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = evaluationResult.forExpression("QuantityNotEqCM1M01").value();
        // assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleNotEqJohnJohn").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TupleNotEqJohnJane").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TupleNotEqJohn1John2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeNotEqTodayToday").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeNotEqTodayYesterday").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeNotEq10A10A").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeNotEq10A10P").value();
        assertThat(result, is(true));
    }

    protected CqlCompilerOptions testCompilerOptions() {
        var options = CqlCompilerOptions.defaultOptions();
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);
        return options;
    }

    String toString(List<CqlCompilerException> errors) {
        StringBuilder builder = new StringBuilder();

        for (var e : errors) {
            builder.append(e.toString() + System.lineSeparator());
            if (e.getLocator() != null) {
                builder.append("at" + System.lineSeparator());
                builder.append(e.getLocator().toLocator() + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
