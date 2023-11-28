package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class IntervalOperatorsTest extends CqlTestBase {
//    @Test
//    public void test_fail() {
//
//        Set<String> set = new HashSet<>();
//        set.add("InvalidIntegerIntervalA");
//        EvaluationResult evaluationResult;
//        try {
//            evaluationResult = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"), set, null, null, null, null);
//            Object result = evaluationResult.forExpression("InvalidIntegerIntervalA").value();
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
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"));
        Object result;

        result = evaluationResult.forExpression("IntegerIntervalAfterTrue").value();
        assertThat(result, is(true));



        result = evaluationResult.forExpression("IntegerIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IntegerIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IntegerIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalPointAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalPointAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalAfterPointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalAfterPointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IntegerIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IntegerIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalPointBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalPointBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalBeforePointTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalBeforePointFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestCollapseNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 10, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(12, true, 19, true)));

        result = evaluationResult.forExpression("IntegerIntervalCollapse2").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 19, true)));

        result = evaluationResult.forExpression("IntegerIntervalCollapse3").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 8, true)));

        result = evaluationResult.forExpression("IntegerIntervalCollapse4").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 6, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(8, true, 10, true)));

        result = evaluationResult.forExpression("DecimalIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new BigDecimal("12.0"), true, new BigDecimal("19.0"), true)));

        result = evaluationResult.forExpression("DecimalIntervalCollapse2").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("8.0"), true)));

        result = evaluationResult.forExpression("QuantityIntervalCollapse").value();
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new Quantity().withValue(new BigDecimal("12.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("19.0")).withUnit("g"), true)));

        result = evaluationResult.forExpression("DateTimeCollapse").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 25)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 5, 30)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("DateTimeCollapse2").value();


        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 5, 25)));
        assertThat(((List<?>)result).size(), is(1));

        result = evaluationResult.forExpression("DateTimeCollapse3").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(bigDecimalZoneOffset, 2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(bigDecimalZoneOffset, 2018, 10, 15)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("DateTimeCollapse4").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = evaluationResult.forExpression("DateTimeCollapse5").value();
        System.out.println(result);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = evaluationResult.forExpression("DateTimeCollapse6").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 10, 15)));


        result = evaluationResult.forExpression("TimeCollapse").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Time(17, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Time(22, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("TimeCollapse2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(1));
        result = evaluationResult.forExpression("TestContainsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TestNullElement1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestNullElement2").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestNullElementTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalContainsFalse").value();
        assertThat(result, is(false));

//        result = evaluationResult.forExpression("DateTimeContainsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeContainsFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.forExpression("TimeContainsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TimeContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IntegerIntervalEnd").value();
        assertThat(result, is(10));

        result = evaluationResult.forExpression("DecimalIntervalEnd").value();
        assertThat(result, is(new BigDecimal("10.0")));

        result = evaluationResult.forExpression("QuantityIntervalEnd").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10.0")).withUnit("g")));

        result = evaluationResult.forExpression("DateTimeIntervalEnd").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 5, 2, 0, 0, 0, 0)));

        result = evaluationResult.forExpression("TimeIntervalEnd").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 599)));



//        result = evaluationResult.forExpression("TestEndsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalEndsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("DateTimeEndsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeEndsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeEndsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeEndsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeEndsFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.forExpression("TestEqualNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeEqualFalse").value();
        assertThat(result, is(false));


//        result = evaluationResult.forExpression("TestExceptNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalExcept1to3").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 3, true)));

        result = evaluationResult.forExpression("IntegerIntervalExcept4to6").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(-4, false, 6, false)));

        result = evaluationResult.forExpression("IntegerIntervalExceptNullOutNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalExceptNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DecimalIntervalExcept1to3").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("3.99999999"), true)));

        result = evaluationResult.forExpression("DecimalIntervalExceptNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("QuantityIntervalExcept1to4").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("4.99999999")).withUnit("g"), true)));

        result = evaluationResult.forExpression("Except12").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 2, true)));

        result = evaluationResult.forExpression("ExceptDateTimeInterval").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 6)));

        result = evaluationResult.forExpression("ExceptDateTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 13)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 16)));

        result = evaluationResult.forExpression("ExceptTimeInterval").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(8, 59, 59, 998)));

        result = evaluationResult.forExpression("ExceptTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(11, 0, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(11, 59, 59, 999)));


        result = evaluationResult.forExpression("TestInNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestInNullEnd").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestNullIn").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeInTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeInNullPrecision").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeInNullTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeInNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.forExpression("Issue32Interval").value();
        assertThat(result, is(true));
        result = evaluationResult.forExpression("TestIncludesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeIncludesFalse").value();
        assertThat(result, is(false));
        result = evaluationResult.forExpression("IntegerIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeIncludedInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeIncludedInPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeIncludedInPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalIntersectTest4to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(4, true, 10, true)));

        result = evaluationResult.forExpression("IntegerIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DecimalIntervalIntersectTest4to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("10.0"), true)));

        result = evaluationResult.forExpression("IntegerIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("QuantityIntervalIntersectTest5to10").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));

        result = evaluationResult.forExpression("QuantityIntervalIntersectTestNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeIntersect").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 7)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 10)));


        result = evaluationResult.forExpression("TimeIntersect").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(4, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(6, 59, 59, 999)));


        result = evaluationResult.forExpression("IntegerIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestMeetsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeMeetsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeMeetsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("TestMeetsBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsBeforeNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeMeetsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeMeetsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestMeetsAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsAfterNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeMeetsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeMeetsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeMeetsAfterFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("IntegerIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeNotEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeNotEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TestOnOrAfterDateTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrAfterDateFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrAfterTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrAfterTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrAfterIntegerTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrAfterDecimalFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrAfterQuantityTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TestOnOrBeforeDateTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrBeforeDateFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrBeforeTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrBeforeTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrBeforeIntegerTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOnOrBeforeDecimalFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOnOrBeforeQuantityTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TestOverlapsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeOverlapsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeOverlapsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeOverlapsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeOverlapsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOverlapsBeforeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeOverlapsBeforeTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeOverlapsBeforeNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeOverlapsBeforeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeOverlapsBeforeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TestOverlapsAfterNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeOverlapsAfterTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeOverlapsAfterNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeOverlapsAfterFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeOverlapsAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeOverlapsAfterFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("TestPointFromNull").value();
        Assert.assertTrue(result == null);

        result = evaluationResult.forExpression("TestPointFromInteger").value();
        Assert.assertTrue((Integer) result == 1);

        result = evaluationResult.forExpression("TestPointFromDecimal").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.0")) == 0);

        result = evaluationResult.forExpression("TestPointFromQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")));



        result = evaluationResult.forExpression("TestProperlyIncludesNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperlyIncludesTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperlyIncludesFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperContainsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperContainsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperContainsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TimeProperContainsPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperContainsPrecisionFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperContainsPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TimeProperInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TimeProperInPrecisionTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperInPrecisionFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperInPrecisionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TestProperlyIncludedInNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DateTimeProperlyIncludedInFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeProperlyIncludedInTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeProperlyIncludedInFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("SizeTest").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("SizeTestEquivalent").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("SizeIsNull").value();
        Assert.assertNull(result);

        result = evaluationResult.forExpression("IntegerIntervalStart").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("DecimalIntervalStart").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("QuantityIntervalStart").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g")));

        result = evaluationResult.forExpression("DateTimeIntervalStart").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2016, 5, 1, 0, 0, 0, 0)));

        result = evaluationResult.forExpression("TimeIntervalStart").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));

        result = evaluationResult.forExpression("TestStartsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IntegerIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DecimalIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("DecimalIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("QuantityIntervalStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("QuantityIntervalStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("DateTimeStartsTrue").value();
        assertThat(result, is(true));

//        result = evaluationResult.forExpression("DateTimeStartsNull").value();
//        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeStartsFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("TimeStartsTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("TimeStartsFalse").value();
        assertThat(result, is(false));


        result = evaluationResult.forExpression("TestUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IntegerIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 15, true)));

        result = evaluationResult.forExpression("IntegerIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DecimalIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("15.0"), true)));

        result = evaluationResult.forExpression("DecimalIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("QuantityIntervalUnion1To15").value();
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("15.0")).withUnit("g"), true)));

        result = evaluationResult.forExpression("QuantityIntervalUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DateTimeUnion").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 28)));

        result = evaluationResult.forExpression("DateTimeUnionNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TimeUnion").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(20, 59, 59, 999)));

        result = evaluationResult.forExpression("TimeUnionNull").value();
        assertThat(result, is(nullValue()));


    }
}
