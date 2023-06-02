package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.opencds.cqf.cql.engine.elm.execution.GreaterEvaluator;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlComparisonOperatorsTest extends CqlExecutionTestBase {

    @Test
    public void testBetween() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("BetweenIntTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("SimpleEqTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleEqTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleEqNullTrue").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleEqInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqInt1Int2Long").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqFloat1Float1WithZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqFloat1Float1WithPrecisionAndZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleEqFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleEqFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityEqCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityEqDiffPrecision").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("RatioEqual").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("RatioNotEqual").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = context.resolveExpressionRef("QuantityEqCM1M01").getExpression().evaluate(context);
        // assertThat(result, is(true));

        result = context.resolveExpressionRef("TupleEqJohnJohn").getExpression().evaluate(context);
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
        result = context.resolveExpressionRef("TupleEqJohnJohnFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
        result = context.resolveExpressionRef("TupleEqJohnJohnFalse2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleEqJohnJane").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleEqJohn1John2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleEqDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TupleEqDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleEqTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TupleEqTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeEqTodayToday").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeEqJanJan").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeEqJanJuly").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeEqNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeUTC").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeEqTodayYesterday").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeEq10A10A").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeEq10A10P").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.GreaterEvaluator#evaluate(Context)}
     */
    @Test
    public void testGreater() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("GreaterZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterLong").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterDecZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterDecZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterDecZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterCM0CM0").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterCM0CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterCM0NegCM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        //result = context.resolveExpressionRef("GreaterM1CM1").getExpression().evaluate(context);
        //assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        //result = context.resolveExpressionRef("GreaterM1CM10").getExpression().evaluate(context);
        //assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterAA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterAB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterBA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterAThanAA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterAAThanA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterJackJill").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeGreaterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeGreaterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeGreaterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeGreaterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("UncertaintyGreaterNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("UncertaintyGreaterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("UncertaintyGreaterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        try {
            GreaterEvaluator.greater(1, "one", context);
            Assert.fail();
        }
        catch (CqlException e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.GreaterOrEqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testGreaterOrEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("GreaterOrEqualZZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualZ1Long").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualDecZZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualDecZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualDecZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualCM0CM0").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualCM0CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualCM0NegCM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        // TODO: Quantity unit comparison is not implemented yet
        // result = context.resolveExpressionRef("GreaterOrEqualM1CM1").getExpression().evaluate(context);
        // assertThat(result, is(true));
        //
        // result = context.resolveExpressionRef("GreaterOrEqualM1CM10").getExpression().evaluate(context);
        // assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualAA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualAB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualBA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualAThanAA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("GreaterOrEqualAAThanA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("GreaterOrEqualJackJill").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeGreaterEqTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeGreaterEqTrue2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeGreaterEqFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeGreaterEqTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeGreaterEqTrue2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeGreaterEqFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("UncertaintyGreaterEqualNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("UncertaintyGreaterEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("UncertaintyGreaterEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LessEvaluator#evaluate(Context)}
     */
    @Test
    public void testLess() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("LessZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessZ1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessLong").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessLongNeg").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessZNeg1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessDecZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessDecZ1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessDecZNeg1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessCM0CM0").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessCM0CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessCM0NegCM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        // result = context.resolveExpressionRef("LessM1CM1").getExpression().evaluate(context);
        // assertThat(result, is(false));

        // result = context.resolveExpressionRef("LessM1CM10").getExpression().evaluate(context);
        // assertThat(result, is(false));

        result = context.resolveExpressionRef("LessAA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessAB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessBA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessAThanAA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessAAThanA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessJackJill").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeLessTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeLessFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeLessTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeLessFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("UncertaintyLessNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("UncertaintyLessTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("UncertaintyLessFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.LessOrEqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testLessOrEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("LessOrEqualZZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualZ1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualZ1Long").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualZNeg1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessOrEqualDecZZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualDecZ1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualDecZNeg1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessOrEqualCM0CM0").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualCM0CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualCM0NegCM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: uncomment once quantity unit comparison is implemented
        // result = context.resolveExpressionRef("LessOrEqualM1CM1").getExpression().evaluate(context);
        // assertThat(result, is(false));
        //
        // result = context.resolveExpressionRef("LessOrEqualM1CM10").getExpression().evaluate(context);
        // assertThat(result, is(false));

        result = context.resolveExpressionRef("LessOrEqualAA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualAB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualBA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessOrEqualAThanAA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("LessOrEqualAAThanA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("LessOrEqualJackJill").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeLessEqTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeLessEqTrue2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeLessEqFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeLessEqTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeLessEqTrue2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeLessEqFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("UncertaintyLessEqualNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("UncertaintyLessEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("UncertaintyLessEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator#evaluate(Context)}
     */
    @Test
    public void testEquivalent() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("EquivTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivNullNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivTrueNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivNullTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivStringIgnoreCase").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivFloat1Float1WithZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Float1WithPrecision").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Float1WithPrecisionAndZ").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloatTrailingZero").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivEqCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("RatioEquivalent").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("RatioNotEquivalent").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        //result = context.resolveExpressionRef("EquivEqCM1M01").getExpression().evaluate(context);
        //assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivTupleJohnJohn").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivTupleJohnJohnWithNulls").getExpression().evaluate(context);
        assertThat(result, is(true));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
        result = context.resolveExpressionRef("EquivTupleJohnJohnFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO - this test should actually throw a translation error, but due to a bug in the translator it isn't - remove once bug is resolved
        result = context.resolveExpressionRef("EquivTupleJohnJohnFalse2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivTupleJohnJane").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivTupleJohn1John2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivDateTimeTodayToday").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivDateTimeTodayYesterday").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivTime10A10A").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivTime10A10P").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NotEqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testNotEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("SimpleNotEqTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleNotEqTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleNotEqNullTrue").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SimpleNotEqInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqInt1Int2Long").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("SimpleNotEqFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("SimpleNotEqFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityNotEqCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: Quantity unit comparison is not implemented yet
        //result = context.resolveExpressionRef("QuantityNotEqCM1M01").getExpression().evaluate(context);
        //assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleNotEqJohnJohn").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TupleNotEqJohnJane").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TupleNotEqJohn1John2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeNotEqTodayToday").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeNotEqTodayYesterday").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeNotEq10A10A").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeNotEq10A10P").getExpression().evaluate(context);
        assertThat(result, is(true));
    }
}
