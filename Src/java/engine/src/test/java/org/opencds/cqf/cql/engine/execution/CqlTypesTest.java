package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlTypesTest extends CqlTestBase {

    @Test
    public void test_all_types() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlTypesTest"));
        Object result;


        result = evaluationResult.expressionResults.get("AnyInteger").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("AnyLong").value();
        assertThat(result, is(Long.valueOf("12")));

        result = evaluationResult.expressionResults.get("AnyDecimal").value();
        assertThat(result, is(new BigDecimal("5.0")));

        result = evaluationResult.expressionResults.get("AnyQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")));

        result = evaluationResult.expressionResults.get("AnyDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 4, 4)));

        result = evaluationResult.expressionResults.get("AnyTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(9, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("AnyInterval").value();
        Assert.assertTrue(((Interval) result).equal(new Interval(2, true, 7, true)));

        result = evaluationResult.expressionResults.get("AnyList").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.expressionResults.get("AnyTuple").value();
        assertThat(((Tuple)result).getElements(), is(new HashMap<String, Object>() {{put("id", 5); put("name", "Chris");}}));

        //result = evaluationResult.expressionResults.get("AnyString").value();
        //assertThat(result, is("Chris"));

        result = evaluationResult.expressionResults.get("BooleanTestTrue").value();
        assertThat(result.getClass().getSimpleName(), is("Boolean"));
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("BooleanTestFalse").value();
        assertThat(result.getClass().getSimpleName(), is("Boolean"));
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeLiteral").value();
        Assert.assertTrue(((Code) result).equal(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure")));

        result = evaluationResult.expressionResults.get("CodeLiteral2").value();
        Assert.assertTrue(((Code) result).equal(new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code")));

        result = evaluationResult.expressionResults.get("ConceptTest").value();
        Assert.assertTrue(((Concept) result).equal(new Concept().withCodes(Arrays.asList(new Code().withCode("8480-6").withSystem("http://loinc.org").withVersion("1.0").withDisplay("Systolic blood pressure"), new Code().withCode("1234-5").withSystem("http://example.org").withVersion("1.05").withDisplay("Test Code"))).withDisplay("Type B viral hepatitis")));

        result = evaluationResult.expressionResults.get("DateTimeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeProper").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 7, 7, 6, 25, 33, 910)));

        result = evaluationResult.expressionResults.get("DateTimeIncomplete").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2015, 2, 10)));

        result = evaluationResult.expressionResults.get("DateTimeUncertain").value();
        Assert.assertEquals(((Interval) result).getStart(), 19);
        Assert.assertEquals(((Interval) result).getEnd(), 49);

        result = evaluationResult.expressionResults.get("DateTimeMin").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 1, 1, 1, 0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("DateTimeMax").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 9999, 12, 31, 23, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("DecimalUpperBoundExcept").value();
        assertThat(result, is(new BigDecimal("10000000000000000000000000000000000.00000000")));

        result = evaluationResult.expressionResults.get("DecimalLowerBoundExcept").value();
        assertThat(result, is(new BigDecimal("-10000000000000000000000000000000000.00000000")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = evaluationResult.expressionResults.get("DecimalFractionalTooBig").value();
        assertThat(result, is(new BigDecimal("5.999999999")));

        result = evaluationResult.expressionResults.get("DecimalPi").value();
        assertThat(result, is(new BigDecimal("3.14159265")));

        result = evaluationResult.expressionResults.get("IntegerProper").value();
        assertThat(result, is(5000));

        result = evaluationResult.expressionResults.get("QuantityTest").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));

        result = evaluationResult.expressionResults.get("QuantityTest2").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        // NOTE: This should also return an error as the fractional precision is greater than 8
        result = evaluationResult.expressionResults.get("QuantityFractionalTooBig").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.99999999")).withUnit("g")));

        result = evaluationResult.expressionResults.get("RatioTest").value();
        Assert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("150.2")).withUnit("[lb_av]")));
        Assert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.5589")).withUnit("{eskimo kisses}")));

        result = evaluationResult.expressionResults.get("StringTestEscapeQuotes").value();
        assertThat(result, is("\'I start with a single quote and end with a double quote\""));


        result = evaluationResult.expressionResults.get("TimeProper").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 25, 12, 863)));

        result = evaluationResult.expressionResults.get("TimeAllMax").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeAllMin").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));


    }
}
