package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlAggregateFunctionsTest extends CqlTestBase {

    @Test
    public void test_all_aggregate_function_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAggregateFunctionsTest"));
        Object result = evaluationResult.expressionResults.get("AllTrueAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AllTrueTrueFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AllTrueFalseFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AllTrueAllTrueFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AllTrueAllFalseTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AllTrueNullFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AllTrueEmptyList").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueAllFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AnyTrueAllTrueFalseTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueAllFalseTrueFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueTrueFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueFalseFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueNullFirstThenTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("AnyTrueNullFirstThenFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("AnyTrueEmptyList").value();
        assertThat(result, is(false));

        try {
            result = AnyTrueEvaluator.anyTrue(Arrays.asList("this", "is", "error"));
            Assert.fail();
        }
        catch (InvalidOperatorArgument e) {
            // pass
        }

        try {
            result = AvgEvaluator.avg(Arrays.asList("this", "is", "error"), engineVisitor.getState());
            Assert.fail();
        }
        catch (InvalidOperatorArgument e) {
            // pass
        }


        result = evaluationResult.expressionResults.get("AvgTest1").value();
        assertThat(result, is(new BigDecimal("3.0")));

        result = evaluationResult.expressionResults.get("Product_Long").value();
        assertThat(result, is(100L));

        result = evaluationResult.expressionResults.get("CountTest1").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("CountTestDateTime").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("CountTestTime").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("CountTestNull").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("MaxTestInteger").value();
        assertThat(result, is(90));

        result = evaluationResult.expressionResults.get("MaxTestLong").value();
        assertThat(result, is(90L));

        result = evaluationResult.expressionResults.get("MaxTestString").value();
        assertThat(result, is("zebra"));

        result = evaluationResult.expressionResults.get("MaxTestDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 10, 6)));

        result = evaluationResult.expressionResults.get("MaxTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("MedianTestDecimal").value();
        assertThat(result, is(new BigDecimal("3.5")));

        result = evaluationResult.expressionResults.get("MinTestInteger").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("MinTestLong").value();
        assertThat(result, is(0L));

        result = evaluationResult.expressionResults.get("MinTestString").value();
        assertThat(result, is("bye"));

        result = evaluationResult.expressionResults.get("MinTestDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 9, 5)));

        result = evaluationResult.expressionResults.get("MinTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("ModeTestInteger").value();
        assertThat(result, is(9));
        result = evaluationResult.expressionResults.get("ModeTestDateTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 10, 5)));

        result = evaluationResult.expressionResults.get("ModeTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("ModeTestDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(null, 2012, 9, 5)));

        result = evaluationResult.expressionResults.get("PopStdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.41421356")) == 0); //23730951454746218587388284504413604736328125

        result = evaluationResult.expressionResults.get("PopVarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.0")) == 0);

        result = evaluationResult.expressionResults.get("StdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.58113883")) == 0); //00841897613935316257993690669536590576171875

        result = evaluationResult.expressionResults.get("SumTest1").value();
        assertThat(result, is(new BigDecimal("20.0")));

        result = evaluationResult.expressionResults.get("SumTestLong").value();
        assertThat(result, is(20L));

        result = evaluationResult.expressionResults.get("SumTestQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("15.0")).withUnit("ml")));

        result = evaluationResult.expressionResults.get("SumTestNull").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("VarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.5")) == 0);

    }
}
