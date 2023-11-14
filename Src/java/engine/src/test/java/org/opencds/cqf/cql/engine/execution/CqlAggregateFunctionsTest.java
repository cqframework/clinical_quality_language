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
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var evaluationResult = engine.evaluate(toElmIdentifier("CqlAggregateFunctionsTest"));
        Object result = evaluationResult.forExpression("AllTrueAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AllTrueTrueFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AllTrueFalseFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AllTrueAllTrueFalseTrue").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AllTrueAllFalseTrueFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AllTrueNullFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AllTrueEmptyList").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueAllTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueAllFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AnyTrueAllTrueFalseTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueAllFalseTrueFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueTrueFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueFalseFirst").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueNullFirstThenTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("AnyTrueNullFirstThenFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("AnyTrueEmptyList").value();
        assertThat(result, is(false));

        try {
            result = AnyTrueEvaluator.anyTrue(Arrays.asList("this", "is", "error"));
            Assert.fail();
        }
        catch (InvalidOperatorArgument e) {
            // pass
        }

        try {
            result = AvgEvaluator.avg(Arrays.asList("this", "is", "error"), engine.getState());
            Assert.fail();
        }
        catch (InvalidOperatorArgument e) {
            // pass
        }


        result = evaluationResult.forExpression("AvgTest1").value();
        assertThat(result, is(new BigDecimal("3.0")));

        result = evaluationResult.expressionResults.get("Product_Long").value();
        assertThat(result, is(100L));

        result = evaluationResult.forExpression("CountTest1").value();
        assertThat(result, is(4));

        result = evaluationResult.forExpression("CountTestDateTime").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("CountTestTime").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("CountTestNull").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("MaxTestInteger").value();
        assertThat(result, is(90));

        result = evaluationResult.forExpression("MaxTestLong").value();
        assertThat(result, is(90L));

        result = evaluationResult.forExpression("MaxTestString").value();
        assertThat(result, is("zebra"));

        result = evaluationResult.forExpression("MaxTestDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 10, 6)));

        result = evaluationResult.forExpression("MaxTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));


        result = evaluationResult.forExpression("MedianTestDecimal").value();
        assertThat(result, is(new BigDecimal("3.5")));

        result = evaluationResult.forExpression("MinTestInteger").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("MinTestLong").value();
        assertThat(result, is(0L));

        result = evaluationResult.forExpression("MinTestString").value();
        assertThat(result, is("bye"));

        result = evaluationResult.forExpression("MinTestDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 9, 5)));

        result = evaluationResult.forExpression("MinTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 59, 59, 999)));


        result = evaluationResult.forExpression("ModeTestInteger").value();
        assertThat(result, is(9));
        result = evaluationResult.forExpression("ModeTestDateTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));

        result = evaluationResult.forExpression("ModeTestTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(5, 59, 59, 999)));

        result = evaluationResult.forExpression("ModeTestDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(bigDecimalZoneOffset, 2012, 9, 5)));

        result = evaluationResult.forExpression("PopStdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.41421356")) == 0); //23730951454746218587388284504413604736328125

        result = evaluationResult.forExpression("PopVarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.0")) == 0);

        result = evaluationResult.forExpression("StdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.58113883")) == 0); //00841897613935316257993690669536590576171875

        result = evaluationResult.forExpression("SumTest1").value();
        assertThat(result, is(new BigDecimal("20.0")));

        result = evaluationResult.forExpression("SumTestLong").value();
        assertThat(result, is(20L));

        result = evaluationResult.forExpression("SumTestQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("15.0")).withUnit("ml")));

        result = evaluationResult.forExpression("SumTestNull").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("VarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.5")) == 0);

    }
}
