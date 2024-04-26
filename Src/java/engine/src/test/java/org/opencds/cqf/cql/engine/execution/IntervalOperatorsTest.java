package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Date;

class IntervalOperatorsTest extends CqlTestBase {
    //    @Test
    //    public void test_fail() {
    //
    //        Set<String> set = new HashSet<>();
    //        set.add("InvalidIntegerIntervalA");
    //
    //        try {
    //            results = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"), set, null, null, null,
    // null);
    //            Object value = results.forExpression("InvalidIntegerIntervalA").value();
    //            Assertions.fail();
    //
    //
    //        } catch (Exception e) {
    //
    //        }
    //
    //    }

    @Test
    void all_interval_operators() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var results = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"));
        var value = results.forExpression("IntegerIntervalAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalPointAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalPointAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalAfterPointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalAfterPointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalPointAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalPointAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalAfterPointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalAfterPointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalPointAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalPointAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalAfterPointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalAfterPointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestBeforeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalPointBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalPointBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalBeforePointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalBeforePointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalPointBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalPointBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalBeforePointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalBeforePointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalPointBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalPointBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalBeforePointTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalBeforePointFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestCollapseNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalCollapse").value();
        assertTrue(((Interval) ((List<?>) value).get(0)).equal(new Interval(1, true, 10, true)));
        assertTrue(((Interval) ((List<?>) value).get(1)).equal(new Interval(12, true, 19, true)));

        value = results.forExpression("IntegerIntervalCollapse2").value();
        assertTrue(((Interval) ((List<?>) value).get(0)).equal(new Interval(1, true, 19, true)));

        value = results.forExpression("IntegerIntervalCollapse3").value();
        assertTrue(((Interval) ((List<?>) value).get(0)).equal(new Interval(4, true, 8, true)));

        value = results.forExpression("IntegerIntervalCollapse4").value();
        assertTrue(((Interval) ((List<?>) value).get(0)).equal(new Interval(4, true, 6, true)));
        assertTrue(((Interval) ((List<?>) value).get(1)).equal(new Interval(8, true, 10, true)));

        value = results.forExpression("DecimalIntervalCollapse").value();
        assertTrue(((Interval) ((List<?>) value).get(0))
                .equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));
        assertTrue(((Interval) ((List<?>) value).get(1))
                .equal(new Interval(new BigDecimal("12.0"), true, new BigDecimal("19.0"), true)));

        value = results.forExpression("DecimalIntervalCollapse2").value();
        assertTrue(((Interval) ((List<?>) value).get(0))
                .equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("8.0"), true)));

        value = results.forExpression("QuantityIntervalCollapse").value();
        assertTrue(((Interval) ((List<?>) value).get(0))
                .equal(new Interval(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"),
                        true,
                        new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"),
                        true)));
        assertTrue(((Interval) ((List<?>) value).get(1))
                .equal(new Interval(
                        new Quantity().withValue(new BigDecimal("12.0")).withUnit("g"),
                        true,
                        new Quantity().withValue(new BigDecimal("19.0")).withUnit("g"),
                        true)));

        value = results.forExpression("DateTimeCollapse").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 25)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 5, 30)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("DateTimeCollapse2").value();

        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 5, 25)));
        assertThat(((List<?>) value).size(), is(1));

        value = results.forExpression("DateTimeCollapse3").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getStart(), new DateTime(bigDecimalZoneOffset, 2018, 1, 1)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getEnd(), new DateTime(bigDecimalZoneOffset, 2018, 8, 28)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getStart(), new DateTime(bigDecimalZoneOffset, 2018, 8, 30)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getEnd(), new DateTime(bigDecimalZoneOffset, 2018, 10, 15)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("DateTimeCollapse4").value();
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getStart(), new Date(2018, 1, 1)));
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getEnd(), new Date(2018, 8, 28)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getStart(), new Date(2018, 8, 30)));
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(1)).getEnd(), new Date(2018, 10, 15)));

        value = results.forExpression("DateTimeCollapse5").value();
        System.out.println(value);
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getStart(), new Date(2018, 1, 1)));
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getEnd(), new Date(2018, 8, 28)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getStart(), new Date(2018, 8, 30)));
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(1)).getEnd(), new Date(2018, 10, 15)));

        value = results.forExpression("DateTimeCollapse6").value();
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getStart(), new Date(2018, 1, 1)));
        assertTrue(
                EquivalentEvaluator.equivalent(((Interval) ((List<?>) value).get(0)).getEnd(), new Date(2018, 10, 15)));

        value = results.forExpression("TimeCollapse").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getStart(), new Time(1, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getStart(), new Time(17, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(1)).getEnd(), new Time(22, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("TimeCollapse2").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getStart(), new Time(1, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) ((List<?>) value).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(1));
        value = results.forExpression("TestContainsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TestNullElement1").value();
        assertThat(value, is(false));

        value = results.forExpression("TestNullElement2").value();
        assertThat(value, is(false));

        value = results.forExpression("TestNullElementTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalContainsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalContainsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalContainsFalse").value();
        assertThat(value, is(false));

        //        value = results.forExpression("DateTimeContainsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeContainsFalse").value();
        assertThat(value, is(false));

        //        value = results.forExpression("TimeContainsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("TimeContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeContainsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalEnd").value();
        assertThat(value, is(10));

        value = results.forExpression("DecimalIntervalEnd").value();
        assertThat(value, is(new BigDecimal("10.0")));

        value = results.forExpression("QuantityIntervalEnd").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("10.0")).withUnit("g")));

        value = results.forExpression("DateTimeIntervalEnd").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 5, 2, 0, 0, 0, 0)));

        value = results.forExpression("TimeIntervalEnd").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(23, 59, 59, 599)));

        //        value = results.forExpression("TestEndsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalEndsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalEndsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalEndsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalEndsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalEndsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalEndsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeEndsTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeEndsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeEndsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeEndsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeEndsFalse").value();
        assertThat(value, is(false));

        //        value = results.forExpression("TestEqualNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeEqualFalse").value();
        assertThat(value, is(false));

        //        value = results.forExpression("TestExceptNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalExcept1to3").value();
        assertTrue(((Interval) value).equal(new Interval(1, true, 3, true)));

        value = results.forExpression("IntegerIntervalExcept4to6").value();
        assertTrue(((Interval) value).equal(new Interval(-4, false, 6, false)));

        value = results.forExpression("IntegerIntervalExceptNullOutNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalExceptNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DecimalIntervalExcept1to3").value();
        assertTrue(((Interval) value)
                .equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("3.99999999"), true)));

        value = results.forExpression("DecimalIntervalExceptNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("QuantityIntervalExcept1to4").value();
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"),
                        true,
                        new Quantity().withValue(new BigDecimal("4.99999999")).withUnit("g"),
                        true)));

        value = results.forExpression("Except12").value();
        assertTrue(((Interval) value).equal(new Interval(1, true, 2, true)));

        value = results.forExpression("ExceptDateTimeInterval").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 5)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 6)));

        value = results.forExpression("ExceptDateTime2").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 13)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 16)));

        value = results.forExpression("ExceptTimeInterval").value();
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getStart(), new Time(5, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getEnd(), new Time(8, 59, 59, 998)));

        value = results.forExpression("ExceptTime2").value();
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getStart(), new Time(11, 0, 0, 0)));
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getEnd(), new Time(11, 59, 59, 999)));

        value = results.forExpression("TestInNull").value();
        assertThat(value, is(false));

        value = results.forExpression("TestInNullEnd").value();
        assertThat(value, is(true));

        value = results.forExpression("TestNullIn").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeInTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeInNullPrecision").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeInNullTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeInNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("Issue32Interval").value();
        assertThat(value, is(true));
        value = results.forExpression("TestIncludesNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeIncludesFalse").value();
        assertThat(value, is(false));
        value = results.forExpression("IntegerIntervalIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeIncludedInNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeIncludedInPrecisionTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeIncludedInPrecisionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalIntersectTest4to10").value();
        assertTrue(((Interval) value).equal(new Interval(4, true, 10, true)));

        value = results.forExpression("IntegerIntervalIntersectTestNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DecimalIntervalIntersectTest4to10").value();
        assertTrue(((Interval) value).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("10.0"), true)));

        value = results.forExpression("IntegerIntervalIntersectTestNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("QuantityIntervalIntersectTest5to10").value();
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new Quantity().withValue(new BigDecimal("5.0")).withUnit("g"),
                        true,
                        new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"),
                        true)));

        value = results.forExpression("QuantityIntervalIntersectTestNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeIntersect").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 7)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 10)));

        value = results.forExpression("TimeIntersect").value();
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getStart(), new Time(4, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getEnd(), new Time(6, 59, 59, 999)));

        value = results.forExpression("IntegerIntervalEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeEquivalentTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeEquivalentFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestMeetsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalMeetsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalMeetsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalMeetsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalMeetsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalMeetsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalMeetsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeMeetsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsNull").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeMeetsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeMeetsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestMeetsBeforeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalMeetsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalMeetsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalMeetsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalMeetsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalMeetsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalMeetsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeMeetsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsBeforeNull").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeMeetsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeMeetsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestMeetsAfterNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalMeetsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalMeetsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalMeetsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalMeetsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalMeetsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalMeetsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeMeetsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsAfterNull").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeMeetsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeMeetsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeMeetsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IntegerIntervalNotEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalNotEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalNotEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalNotEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalNotEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalNotEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeNotEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeNotEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeNotEqualTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeNotEqualFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrAfterNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TestOnOrAfterDateTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrAfterDateFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrAfterTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrAfterTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrAfterIntegerTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrAfterDecimalFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrAfterQuantityTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrBeforeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TestOnOrBeforeDateTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrBeforeDateFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrBeforeTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrBeforeTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrBeforeIntegerTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOnOrBeforeDecimalFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOnOrBeforeQuantityTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TestOverlapsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalOverlapsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalOverlapsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalOverlapsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalOverlapsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalOverlapsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalOverlapsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeOverlapsTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeOverlapsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeOverlapsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeOverlapsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeOverlapsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOverlapsBeforeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalOverlapsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalOverlapsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalOverlapsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalOverlapsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalOverlapsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalOverlapsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeOverlapsBeforeTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeOverlapsBeforeNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeOverlapsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeOverlapsBeforeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeOverlapsBeforeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestOverlapsAfterNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalOverlapsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalOverlapsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalOverlapsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalOverlapsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalOverlapsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalOverlapsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeOverlapsAfterTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeOverlapsAfterNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeOverlapsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeOverlapsAfterTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeOverlapsAfterFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestPointFromNull").value();
        assertTrue(value == null);

        value = results.forExpression("TestPointFromInteger").value();
        assertTrue((Integer) value == 1);

        value = results.forExpression("TestPointFromDecimal").value();
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal("1.0")));

        value = results.forExpression("TestPointFromQuantity").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")));

        value = results.forExpression("TestProperlyIncludesNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalProperlyIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalProperlyIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalProperlyIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalProperlyIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalProperlyIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalProperlyIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeProperlyIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeProperlyIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperlyIncludesTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperlyIncludesFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperContainsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperContainsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperContainsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TimeProperContainsPrecisionTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperContainsPrecisionFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperContainsPrecisionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TimeProperInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperInNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TimeProperInPrecisionTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperInPrecisionFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperInPrecisionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TestProperlyIncludedInNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalProperlyIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalProperlyIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalProperlyIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalProperlyIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalProperlyIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalProperlyIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeProperlyIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DateTimeProperlyIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeProperlyIncludedInTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeProperlyIncludedInFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("SizeTest").value();
        assertThat(value, is(5));

        value = results.forExpression("SizeTestEquivalent").value();
        assertThat(value, is(5));

        value = results.forExpression("SizeIsNull").value();
        assertNull(value);

        value = results.forExpression("IntegerIntervalStart").value();
        assertThat(value, is(1));

        value = results.forExpression("DecimalIntervalStart").value();
        assertThat(value, is(new BigDecimal("1.0")));

        value = results.forExpression("QuantityIntervalStart").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g")));

        value = results.forExpression("DateTimeIntervalStart").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2016, 5, 1, 0, 0, 0, 0)));

        value = results.forExpression("TimeIntervalStart").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(0, 0, 0, 0)));

        value = results.forExpression("TestStartsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalStartsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IntegerIntervalStartsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DecimalIntervalStartsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("DecimalIntervalStartsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("QuantityIntervalStartsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("QuantityIntervalStartsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("DateTimeStartsTrue").value();
        assertThat(value, is(true));

        //        value = results.forExpression("DateTimeStartsNull").value();
        //        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeStartsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TimeStartsTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("TimeStartsFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("TestUnionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IntegerIntervalUnion1To15").value();
        assertTrue(((Interval) value).equal(new Interval(1, true, 15, true)));

        value = results.forExpression("IntegerIntervalUnionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DecimalIntervalUnion1To15").value();
        assertTrue(((Interval) value).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("15.0"), true)));

        value = results.forExpression("DecimalIntervalUnionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("QuantityIntervalUnion1To15").value();
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"),
                        true,
                        new Quantity().withValue(new BigDecimal("15.0")).withUnit("g"),
                        true)));

        value = results.forExpression("QuantityIntervalUnionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DateTimeUnion").value();
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getStart(), new DateTime(bigDecimalZoneOffset, 2012, 1, 5)));
        assertTrue(EquivalentEvaluator.equivalent(
                ((Interval) value).getEnd(), new DateTime(bigDecimalZoneOffset, 2012, 1, 28)));

        value = results.forExpression("DateTimeUnionNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TimeUnion").value();
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getStart(), new Time(5, 59, 59, 999)));
        assertTrue(EquivalentEvaluator.equivalent(((Interval) value).getEnd(), new Time(20, 59, 59, 999)));

        value = results.forExpression("TimeUnionNull").value();
        assertThat(value, is(nullValue()));
    }
}
