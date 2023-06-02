package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidDateTime;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Concept;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Ratio;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;


public class CqlTypesTest extends CqlExecutionTestBase {

    @Test
    @SuppressWarnings("serial")
    public void testAny() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("AnyInteger").getExpression().evaluate(context);
        assertThat(result, is(5));

        result = context.resolveExpressionRef("AnyLong").getExpression().evaluate(context);
        assertThat(result, is(Long.valueOf("12")));

        result = context.resolveExpressionRef("AnyDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("5.0")));

        result = context.resolveExpressionRef("AnyQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")));

        result = context.resolveExpressionRef("AnyDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 4, 4)));

        result = context.resolveExpressionRef("AnyTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(9, 0, 0, 0)));

        result = context.resolveExpressionRef("AnyInterval").getExpression().evaluate(context);
        Assert.assertTrue(((Interval) result).equal(new Interval(2, true, 7, true)));

        result = context.resolveExpressionRef("AnyList").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef("AnyTuple").getExpression().evaluate(context);
        assertThat(((Tuple)result).getElements(), is(new HashMap<String, Object>() {{put("id", 5); put("name", "Chris");}}));

        result = context.resolveExpressionRef("AnyString").getExpression().evaluate(context);
        assertThat(result, is("Chris"));
    }

    @Test
    public void testBoolean() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("BooleanTestTrue").getExpression().evaluate(context);
        assertThat(result.getClass().getSimpleName(), is("Boolean"));
        assertThat(result, is(true));

        result = context.resolveExpressionRef("BooleanTestFalse").getExpression().evaluate(context);
        assertThat(result.getClass().getSimpleName(), is("Boolean"));
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CodeEvaluator#evaluate(Context)}
     */
    @Test
    public void testCode() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("CodeLiteral").getExpression().evaluate(context);
        Assert.assertTrue(((Code) result).equal(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure")));

        result = context.resolveExpressionRef("CodeLiteral2").getExpression().evaluate(context);
        Assert.assertTrue(((Code) result).equal(new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ConceptEvaluator#evaluate(Context)}
     */
    @Test
    public void testConcept() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ConceptTest").getExpression().evaluate(context);
        Assert.assertTrue(((Concept) result).equal(new Concept().withCodes(Arrays.asList(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure"), new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code"))).withDisplay("Type B viral hepatitis")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.DateTimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testDateTime() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("DateTimeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        try {
            context.resolveExpressionRef("DateTimeUpperBoundExcept").getExpression().evaluate(context);
            Assert.fail();
        }
        catch (InvalidDateTime e) {
            // pass
        }

        try {
            context.resolveExpressionRef("DateTimeLowerBoundExcept").getExpression().evaluate(context);
            Assert.fail();
        }
        catch (InvalidDateTime e) {
            // pass
        }

        result = context.resolveExpressionRef("DateTimeProper").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 7, 7, 6, 25, 33, 910)));

        result = context.resolveExpressionRef("DateTimeIncomplete").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2015, 2, 10)));

        result = context.resolveExpressionRef("DateTimeUncertain").getExpression().evaluate(context);
        Assert.assertEquals(((Interval) result).getStart(), 19);
        Assert.assertEquals(((Interval) result).getEnd(), 49);

        result = context.resolveExpressionRef("DateTimeMin").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1, 1, 1, 0, 0, 0, 0)));

        result = context.resolveExpressionRef("DateTimeMax").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 9999, 12, 31, 23, 59, 59, 999)));
    }

    @Test
    public void testDecimal() {
        Context context = new Context(library);
        // NOTE: these should result in compile-time decimal number is too large error, but they do not...
        Object result = context.resolveExpressionRef("DecimalUpperBoundExcept").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("10000000000000000000000000000000000.00000000")));

        result = context.resolveExpressionRef("DecimalLowerBoundExcept").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("-10000000000000000000000000000000000.00000000")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = context.resolveExpressionRef("DecimalFractionalTooBig").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("5.999999999")));

        result = context.resolveExpressionRef("DecimalPi").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("3.14159265")));
    }

    @Test
    public void testInteger() {
        Context context = new Context(library);
        // NOTE: These result in compile-time integer number is too large error, which is correct
        // Object result = context.resolveExpressionRef("IntegerUpperBoundExcept").getExpression().evaluate(context);
        // assertThat(result, is(new Integer(2147483649)));
        //
        // result = context.resolveExpressionRef("IntegerLowerBoundExcept").getExpression().evaluate(context);
        // assertThat(result, is(new Integer(-2147483649)));

        Object result = context.resolveExpressionRef("IntegerProper").getExpression().evaluate(context);
        assertThat(result, is(5000));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.QuantityEvaluator#evaluate(Context)}
     */
    @Test
    public void testQuantity() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("QuantityTest").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));

        result = context.resolveExpressionRef("QuantityTest2").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = context.resolveExpressionRef("QuantityFractionalTooBig").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.99999999")).withUnit("g")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.RatioEvaluator#evaluate(Context)}
     */
    @Test
    public void testRatio() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("RatioTest").getExpression().evaluate(context);
        Assert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));
        Assert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));
    }

    @Test
    public void testString() {
        Context context = new Context(library);
        // NOTE: The escape characters (i.e. the backslashes) remain in the string...
        Object result = context.resolveExpressionRef("StringTestEscapeQuotes").getExpression().evaluate(context);
        assertThat(result, is("\'I start with a single quote and end with a double quote\""));

        // NOTE: This test returns "\u0048\u0069" instead of the string equivalent "Hi"
        // result = context.resolveExpressionRef("StringUnicodeTest").getExpression().evaluate(context);
        // assertThat(result, is(new String("Hi")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.TimeEvaluator#evaluate(Context)}
     */
    @Test
    public void testTime() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TimeProper").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 25, 12, 863)));

        result = context.resolveExpressionRef("TimeAllMax").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = context.resolveExpressionRef("TimeAllMin").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));
    }
}
