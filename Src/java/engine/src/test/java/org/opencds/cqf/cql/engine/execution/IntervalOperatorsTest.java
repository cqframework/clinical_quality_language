package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.comparesEqualTo;

public class IntervalOperatorsTest extends CqlTestBase {
//    @Test
//    public void test_fail() {
//
//        Set<String> set = new HashSet<>();
//        set.add("InvalidIntegerIntervalA");
//        EvaluationResult evaluationResult;
//        try {
//            evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"), set, null, null, null, null);
//            Object result = evaluationResult.expressionResults.get("InvalidIntegerIntervalA").value();
//            Assert.fail();
//
//
//        } catch (Exception e) {
//
//        }
//
//    }

    @Test
    public void test_all_interval_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"));
        Object result;

        result = evaluationResult.expressionResults.get("IntegerIntervalAfterTrue").value();
        assertThat(result, is(true));



        result = evaluationResult.expressionResults.get("IntegerIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IntegerIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IntegerIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IntegerIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IntegerIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestCollapseNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 10, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(12, true, 19, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalCollapse2").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 19, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalCollapse3").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 8, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalCollapse4").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 6, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(8, true, 10, true)));

        result = evaluationResult.expressionResults.get("DecimalIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new BigDecimal("12.0"), true, new BigDecimal("19.0"), true)));

        result = evaluationResult.expressionResults.get("DecimalIntervalCollapse2").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("8.0"), true)));

        result = evaluationResult.expressionResults.get("QuantityIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new Quantity().withValue(new BigDecimal("12.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("19.0")).withUnit("g"), true)));

        result = evaluationResult.expressionResults.get("DateTimeCollapse").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2012, 1, 25)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(null, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(null, 2012, 5, 30)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.expressionResults.get("DateTimeCollapse2").value();


        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2012, 5, 25)));
        assertThat(((List<?>)result).size(), is(1));

        result = evaluationResult.expressionResults.get("DateTimeCollapse3").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(null, 2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(null, 2018, 10, 15)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.expressionResults.get("DateTimeCollapse4").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = evaluationResult.expressionResults.get("DateTimeCollapse5").value();
        System.out.println(result);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = evaluationResult.expressionResults.get("DateTimeCollapse6").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 10, 15)));


        result = evaluationResult.expressionResults.get("TimeCollapse").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Time(17, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Time(22, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.expressionResults.get("TimeCollapse2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(1));
        result = evaluationResult.expressionResults.get("TestContainsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TestNullElement1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestNullElement2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestNullElementTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalContainsFalse").value();
        assertThat(result, is(false));

//        result = evaluationResult.expressionResults.get("DateTimeContainsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeContainsFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.expressionResults.get("TimeContainsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TimeContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IntegerIntervalEnd").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("DecimalIntervalEnd").value();
        assertThat(result, is(new BigDecimal("10.0")));

        result = evaluationResult.expressionResults.get("QuantityIntervalEnd").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10.0")).withUnit("g")));

        result = evaluationResult.expressionResults.get("DateTimeIntervalEnd").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 2, 0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TimeIntervalEnd").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 599)));



//        result = evaluationResult.expressionResults.get("TestEndsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalEndsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("DateTimeEndsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeEndsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeEndsFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.expressionResults.get("TestEqualNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeEqualFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.expressionResults.get("TestExceptNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalExcept1to3").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 3, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalExcept4to6").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(-4, false, 6, false)));

        result = evaluationResult.expressionResults.get("IntegerIntervalExceptNullOutNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalExceptNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DecimalIntervalExcept1to3").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("3.99999999"), true)));

        result = evaluationResult.expressionResults.get("DecimalIntervalExceptNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("QuantityIntervalExcept1to4").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("4.99999999")).withUnit("g"), true)));

        result = evaluationResult.expressionResults.get("Except12").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 2, true)));

        result = evaluationResult.expressionResults.get("ExceptDateTimeInterval").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 6)));

        result = evaluationResult.expressionResults.get("ExceptDateTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 13)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 16)));

        result = evaluationResult.expressionResults.get("ExceptTimeInterval").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(8, 59, 59, 998)));

        result = evaluationResult.expressionResults.get("ExceptTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(11, 0, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(11, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("TestInNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestInNullEnd").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestNullIn").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeInTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeInNullPrecision").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeInNullTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeInNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Issue32Interval").value();
        assertThat(result, is(true));
        result = evaluationResult.expressionResults.get("TestIncludesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeIncludesFalse").value();
        assertThat(result, is(false));
        result = evaluationResult.expressionResults.get("IntegerIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeIncludedInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeIncludedInPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeIncludedInPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalIntersectTest4to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(4, true, 10, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DecimalIntervalIntersectTest4to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("10.0"), true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("QuantityIntervalIntersectTest5to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));

        result = evaluationResult.expressionResults.get("QuantityIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeIntersect").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 7)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 10)));


        result = evaluationResult.expressionResults.get("TimeIntersect").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(4, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(6, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("IntegerIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestMeetsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeMeetsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("TestMeetsBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsBeforeNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestMeetsAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsAfterNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeMeetsAfterFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("IntegerIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TestOnOrAfterDateTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrAfterDateFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrAfterTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrAfterTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrAfterIntegerTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrAfterDecimalFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrAfterQuantityTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeDateTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeDateFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeIntegerTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeDecimalFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOnOrBeforeQuantityTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TestOverlapsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeOverlapsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOverlapsBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsBeforeTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeOverlapsBeforeNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TestOverlapsAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsAfterTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeOverlapsAfterNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeOverlapsAfterFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("TestPointFromNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.expressionResults.get("TestPointFromInteger").value();
        Assert.assertTrue((Integer) result == 1);

        result = evaluationResult.expressionResults.get("TestPointFromDecimal").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.0")) == 0);

        result = evaluationResult.expressionResults.get("TestPointFromQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")));



        result = evaluationResult.expressionResults.get("TestProperlyIncludesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperContainsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TimeProperContainsPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperContainsPrecisionFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperContainsPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TimeProperInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TimeProperInPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperInPrecisionFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperInPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TestProperlyIncludedInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeProperlyIncludedInFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("SizeTest").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("SizeTestEquivalent").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("SizeIsNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("IntegerIntervalStart").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DecimalIntervalStart").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("QuantityIntervalStart").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g")));

        result = evaluationResult.expressionResults.get("DateTimeIntervalStart").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 1, 0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TimeIntervalStart").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TestStartsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IntegerIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DecimalIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DecimalIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("QuantityIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("QuantityIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeStartsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.expressionResults.get("DateTimeStartsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeStartsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("TestUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IntegerIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 15, true)));

        result = evaluationResult.expressionResults.get("IntegerIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DecimalIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("15.0"), true)));

        result = evaluationResult.expressionResults.get("DecimalIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("QuantityIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("15.0")).withUnit("g"), true)));

        result = evaluationResult.expressionResults.get("QuantityIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeUnion").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 28)));

        result = evaluationResult.expressionResults.get("DateTimeUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TimeUnion").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeUnionNull").value();
        assertThat(result, is(nullValue()));


    }
}
