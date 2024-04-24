package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.GreaterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;

class CqlComparisonOperatorsTest extends CqlTestBase {

    @Test
    void cql_comparison_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlComparisonOperatorsTest"), errors, testCompilerOptions());
        assertFalse(
                CqlCompilerException.hasErrors(errors),
                String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    void all_comparison_operators_tests() {
        var eng = getEngine(testCompilerOptions());
        var results = eng.evaluate(toElmIdentifier("CqlComparisonOperatorsTest"));

        Object value = results.forExpression("BetweenIntTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqTrueTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqTrueFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqFalseFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqFalseTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleEqTrueNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleEqNullTrue").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleEqInt1Int1").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqInt1Int2").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqInt1Int2Long").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqStringAStringA").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqStringAStringB").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqFloat1Float1").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqFloat1Float1WithZ").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqFloat1Float1WithPrecisionAndZ").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqFloat1Float2").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleEqFloat1Int1").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleEqFloat1Int2").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityEqCM1CM1").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityEqDiffPrecision").value();
        assertThat(value, is(true));

        value = results.forExpression("RatioEqual").value();
        assertThat(value, is(true));

        value = results.forExpression("RatioNotEqual").value();
        assertThat(value, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("QuantityEqCM1M01").value();
        // assertThat(value, is(true));

        value = results.forExpression("TupleEqJohnJohn").value();
        assertThat(value, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        value = results.forExpression("TupleEqJohnJohnFalse").value();
        //        assertThat(value, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        value = results.forExpression("TupleEqJohnJohnFalse2").value();
        //        assertThat(value, is(false));

        value = results.forExpression("TupleEqJohnJane").value();
        assertThat(value, is(false));

        value = results.forExpression("TupleEqJohn1John2").value();
        assertThat(value, is(false));

        value = results.forExpression("TupleEqDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TupleEqDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TupleEqTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TupleEqTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeEqTodayToday").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeEqJanJan").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeEqJanJuly").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeEqNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeUTC").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeEqTodayYesterday").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeEq10A10A").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeEq10A10P").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterZZ").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterLong").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterZ1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterZNeg1").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterDecZZ").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterDecZ1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterDecZNeg1").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterCM0CM0").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterCM0CM1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterCM0NegCM1").value();
        assertThat(value, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("GreaterM1CM1").value();
        // assertThat(value, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("GreaterM1CM10").value();
        // assertThat(value, is(true));

        value = results.forExpression("GreaterAA").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterAB").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterBA").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterAThanAA").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterAAThanA").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterJackJill").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeGreaterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeGreaterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeGreaterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeGreaterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("UncertaintyGreaterNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("UncertaintyGreaterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("UncertaintyGreaterFalse").value();
        assertThat(value, is(false));

        try {
            GreaterEvaluator.greater(1, "one", engine.getState());
            fail();
        } catch (CqlException e) {
            // pass
        }

        value = results.forExpression("GreaterOrEqualZZ").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualZ1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualZ1Long").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualZNeg1").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualDecZZ").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualDecZ1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualDecZNeg1").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualCM0CM0").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualCM0CM1").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualCM0NegCM1").value();
        assertThat(value, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("GreaterOrEqualM1CM1").value();
        // assertThat(value, is(true));
        //
        // value = results.forExpression("GreaterOrEqualM1CM10").value();
        // assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualAA").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualAB").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualBA").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualAThanAA").value();
        assertThat(value, is(false));

        value = results.forExpression("GreaterOrEqualAAThanA").value();
        assertThat(value, is(true));

        value = results.forExpression("GreaterOrEqualJackJill").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeGreaterEqTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeGreaterEqTrue2").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeGreaterEqFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeGreaterEqTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeGreaterEqTrue2").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeGreaterEqFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("UncertaintyGreaterEqualNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("UncertaintyGreaterEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("UncertaintyGreaterEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("LessZZ").value();
        assertThat(value, is(false));

        value = results.forExpression("LessZ1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessLong").value();
        assertThat(value, is(true));

        value = results.forExpression("LessLongNeg").value();
        assertThat(value, is(true));

        value = results.forExpression("LessZNeg1").value();
        assertThat(value, is(false));

        value = results.forExpression("LessDecZZ").value();
        assertThat(value, is(false));

        value = results.forExpression("LessDecZ1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessDecZNeg1").value();
        assertThat(value, is(false));

        value = results.forExpression("LessCM0CM0").value();
        assertThat(value, is(false));

        value = results.forExpression("LessCM0CM1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessCM0NegCM1").value();
        assertThat(value, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("LessM1CM1").value();
        // assertThat(value, is(false));

        // value = results.forExpression("LessM1CM10").value();
        // assertThat(value, is(false));

        value = results.forExpression("LessAA").value();
        assertThat(value, is(false));

        value = results.forExpression("LessAB").value();
        assertThat(value, is(true));

        value = results.forExpression("LessBA").value();
        assertThat(value, is(false));

        value = results.forExpression("LessAThanAA").value();
        assertThat(value, is(true));

        value = results.forExpression("LessAAThanA").value();
        assertThat(value, is(false));

        value = results.forExpression("LessJackJill").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeLessTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeLessFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeLessTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeLessFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("UncertaintyLessNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("UncertaintyLessTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("UncertaintyLessFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("LessOrEqualZZ").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualZ1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualZ1Long").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualZNeg1").value();
        assertThat(value, is(false));

        value = results.forExpression("LessOrEqualDecZZ").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualDecZ1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualDecZNeg1").value();
        assertThat(value, is(false));

        value = results.forExpression("LessOrEqualCM0CM0").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualCM0CM1").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualCM0NegCM1").value();
        assertThat(value, is(false));

        // TODO: uncomment once quantity unit comparison is implemented
        // value = results.forExpression("LessOrEqualM1CM1").value();
        // assertThat(value, is(false));
        //
        // value = results.forExpression("LessOrEqualM1CM10").value();
        // assertThat(value, is(false));

        value = results.forExpression("LessOrEqualAA").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualAB").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualBA").value();
        assertThat(value, is(false));

        value = results.forExpression("LessOrEqualAThanAA").value();
        assertThat(value, is(true));

        value = results.forExpression("LessOrEqualAAThanA").value();
        assertThat(value, is(false));

        value = results.forExpression("LessOrEqualJackJill").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeLessEqTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeLessEqTrue2").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeLessEqFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeLessEqTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeLessEqTrue2").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeLessEqFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("UncertaintyLessEqualNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("UncertaintyLessEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("UncertaintyLessEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivTrueTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivTrueFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivFalseFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFalseTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivNullNull").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivTrueNull").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivNullTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivInt1Int1").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivInt1Int2").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivStringAStringA").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivStringAStringB").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivStringIgnoreCase").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Float1").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Float2").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivFloat1Float1WithZ").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Float1WithPrecision").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Float1WithPrecisionAndZ").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloatTrailingZero").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Int1").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivFloat1Int2").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivEqCM1CM1").value();
        assertThat(value, is(true));

        value = results.forExpression("RatioEquivalent").value();
        assertThat(value, is(true));

        value = results.forExpression("RatioNotEquivalent").value();
        assertThat(value, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("EquivEqCM1M01").value();
        // assertThat(value, is(true));

        value = results.forExpression("EquivTupleJohnJohn").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivTupleJohnJohnWithNulls").value();
        assertThat(value, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        value = results.forExpression("EquivTupleJohnJohnFalse").value();
        //        assertThat(value, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't -
        // remove once bug is resolved
        //        value = results.forExpression("EquivTupleJohnJohnFalse2").value();
        //        assertThat(value, is(false));

        value = results.forExpression("EquivTupleJohnJane").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivTupleJohn1John2").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivDateTimeTodayToday").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivDateTimeTodayYesterday").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivTime10A10A").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivTime10A10P").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqTrueTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqTrueFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqFalseFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqFalseTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqNullNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleNotEqTrueNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleNotEqNullTrue").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SimpleNotEqInt1Int1").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqInt1Int2").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqInt1Int2Long").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqStringAStringA").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqStringAStringB").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqFloat1Float1").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqFloat1Float2").value();
        assertThat(value, is(true));

        value = results.forExpression("SimpleNotEqFloat1Int1").value();
        assertThat(value, is(false));

        value = results.forExpression("SimpleNotEqFloat1Int2").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityNotEqCM1CM1").value();
        assertThat(value, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // value = results.forExpression("QuantityNotEqCM1M01").value();
        // assertThat(value, is(false));

        value = results.forExpression("TupleNotEqJohnJohn").value();
        assertThat(value, is(false));

        value = results.forExpression("TupleNotEqJohnJane").value();
        assertThat(value, is(true));

        value = results.forExpression("TupleNotEqJohn1John2").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeNotEqTodayToday").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeNotEqTodayYesterday").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeNotEq10A10A").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeNotEq10A10P").value();
        assertThat(value, is(true));
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
