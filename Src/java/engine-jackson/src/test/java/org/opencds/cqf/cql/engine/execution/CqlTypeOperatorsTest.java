package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidCast;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlTypeOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AsEvaluator#evaluate(Context)}
     */
    @Test
    public void testAs() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("AsQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = context.resolveExpressionRef("CastAsQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = context.resolveExpressionRef("AsDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

        try {
            result = context.as(1, Tuple.class, true);
            Assert.fail();
        }
        catch (InvalidCast e) {
            // pass
        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvert() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerToDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal(5)));

        result = context.resolveExpressionRef("IntegerToString").getExpression().evaluate(context);
        assertThat(result, is("5"));

        try {
            context.resolveExpressionRef("StringToIntegerError").getExpression().evaluate(context);
        } catch (NumberFormatException nfe) {
            assertThat(nfe.getMessage(), is("Unable to convert given string to Integer"));
        }

        result = context.resolveExpressionRef("StringToDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

        result = context.resolveExpressionRef("StringToTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

        try {
            context.resolveExpressionRef("StringToDateTimeMalformed").getExpression().evaluate(context);
        } catch (DateTimeParseException iae) {

        }
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertQuantityEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertQuantity() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertQuantity").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

        result = context.resolveExpressionRef("ConvertSyntax").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToBooleanEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToBoolean() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToBooleanTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToBooleanFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToBooleanNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToDateEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToDate() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToDateTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDateFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDateNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToDateTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToDateTime() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToDateTimeStringTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDateTimeDateTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDateTimeFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDateTimeNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToDecimalEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToDecimal() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToDecimalTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDecimalFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToDecimalNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToIntegerEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToInteger() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToIntegerTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToIntegerLong").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToIntegerFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToIntegerNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToLongEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToLong() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToLongTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToLongFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToLongNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToQuantityEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToQuantity() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToQuantityStringTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToQuantityStringFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToQuantityIntegerTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToQuantityDecimalTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToQuantityRatioTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToQuantityNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToStringEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToString() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToStringBoolean").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringInteger").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringLong").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringDecimal").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringQuantity").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringRatio").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringDate").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringDateTime").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringTime").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToStringNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConvertsToTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testConvertsToTime() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConvertsToTimeTrue").getExpression().evaluate(context);
        Assert.assertTrue((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToTimeFalse").getExpression().evaluate(context);
        Assert.assertFalse((Boolean) result);

        result = context.resolveExpressionRef("ConvertsToTimeNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IsEvaluator#evaluate(Context)}
     */
    @Test
    public void testIs() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIsInteger").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("StringIsInteger").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToBooleanEvaluator#evaluate(Context)}
     */
    @Test
    public void testToBoolean() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("StringNoToBoolean").getExpression().evaluate(context);
        assertThat(result, is(false));

    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToConceptEvaluator#evaluate(Context)}
     */
    @Test
    public void testToConcept() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("CodeToConcept1").getExpression().evaluate(context);
        Assert.assertTrue(((Concept) result).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToDateTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testToDateTime() {
        // TODO: Fix timezone tests
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ToDateTime0").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1)));

        result = context.resolveExpressionRef("ToDateTime1").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = context.resolveExpressionRef("ToDateTime2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = context.resolveExpressionRef("ToDateTime3").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = context.resolveExpressionRef("ToDateTime4").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("1.5")));

        result = context.resolveExpressionRef("ToDateTime5").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-1.25")));

        result = context.resolveExpressionRef("ToDateTime6").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal(0), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = context.resolveExpressionRef("ToDateTimeMalformed").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToDecimalEvaluator#evaluate(Context)}
     */
    @Test
    public void testToDecimal() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("String25D5ToDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("25.5")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToIntegerEvaluator#evaluate(Context)}
     */
    @Test
    public void testToInteger() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("StringNeg25ToInteger").getExpression().evaluate(context);
        assertThat(result, is(-25));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToLongEvaluator#evaluate(Context)}
     */
    @Test
    public void testToLong() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("String123ToLong").getExpression().evaluate(context);
        assertThat(result, is(123L));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToQuantityEvaluator#evaluate(Context)}
     */
    @Test
    public void testToQuantity() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("String5D5CMToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

        result = context.resolveExpressionRef("StringInvalidToQuantityNull").getExpression().evaluate(context);
        Assert.assertNull(result);

        result = context.resolveExpressionRef("String100PerMinPerSqMeterToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

        result = context.resolveExpressionRef("String100UnitPer10BillionToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

        result = context.resolveExpressionRef("String60DayPer7DayToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

        result = context.resolveExpressionRef("String60EhrlichPer100gmToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

        result = context.resolveExpressionRef("StringPercentToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

        result = context.resolveExpressionRef("StringPercentWithoutQuoteToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

        result = context.resolveExpressionRef("StringPercentWithTabToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

        result = context.resolveExpressionRef("StringPercentWithMultiSpacesToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

        result = context.resolveExpressionRef("StringPercentWithSpacesUnitToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

        result = context.resolveExpressionRef("StringPercentWithQuoteUnitToQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToRatioEvaluator#evaluate(Context)}
     */
    @Test
    public void testToRatio() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ToRatioIsValid").getExpression().evaluate(context);
        Assert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
        Assert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

        result = context.resolveExpressionRef("ToRatioIsNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToStringEvaluator#evaluate(Context)}
     */
    @Test
    public void testToString() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerNeg5ToString").getExpression().evaluate(context);
        assertThat(result, is("-5"));

        result = context.resolveExpressionRef("LongNeg5ToString").getExpression().evaluate(context);
        assertThat(result, is("-5"));

        result = context.resolveExpressionRef("Decimal18D55ToString").getExpression().evaluate(context);
        assertThat(result, is("18.55"));

        result = context.resolveExpressionRef("Quantity5D5CMToString").getExpression().evaluate(context);
        assertThat(result, is("5.5 'cm'"));

        result = context.resolveExpressionRef("BooleanTrueToString").getExpression().evaluate(context);
        assertThat(result, is("true"));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ToTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testToTime() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ToTime1").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

        result = context.resolveExpressionRef("ToTimeMalformed").getExpression().evaluate(context);
        Assert.assertNull(result);
    }
}
