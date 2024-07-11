package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.elm.executing.AnyTrueEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.AvgEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;

class CqlAggregateFunctionsTest extends CqlTestBase {

    @Test
    void all_aggregate_function_tests() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var results = engine.evaluate(toElmIdentifier("CqlAggregateFunctionsTest"));
        Object value = results.forExpression("AllTrueAllTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("AllTrueTrueFirst").value();
        assertThat(value, is(false));

        value = results.forExpression("AllTrueFalseFirst").value();
        assertThat(value, is(false));

        value = results.forExpression("AllTrueAllTrueFalseTrue").value();
        assertThat(value, is(false));

        value = results.forExpression("AllTrueAllFalseTrueFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("AllTrueNullFirst").value();
        assertThat(value, is(true));

        value = results.forExpression("AllTrueEmptyList").value();
        assertThat(value, is(true));

        value = results.forExpression("AllTrueIsTrueWhenNull").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueAllTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueAllFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("AnyTrueAllTrueFalseTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueAllFalseTrueFalse").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueTrueFirst").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueFalseFirst").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueNullFirstThenTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("AnyTrueNullFirstThenFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("AnyTrueEmptyList").value();
        assertThat(value, is(false));

        value = results.forExpression("AnyTrueIsFalseWhenNull").value();
        assertThat(value, is(false));

        try {
            value = AnyTrueEvaluator.anyTrue(Arrays.asList("this", "is", "error"));
            fail();
        } catch (InvalidOperatorArgument e) {
            // pass
        }

        try {
            value = AvgEvaluator.avg(Arrays.asList("this", "is", "error"), engine.getState());
            fail();
        } catch (InvalidOperatorArgument e) {
            // pass
        }

        value = results.forExpression("AvgTest1").value();
        assertThat(value, is(new BigDecimal("3.0")));

        value = results.expressionResults.get("Product_Long").value();
        assertThat(value, is(100L));

        value = results.forExpression("CountTest1").value();
        assertThat(value, is(4));

        value = results.forExpression("CountTestDateTime").value();
        assertThat(value, is(3));

        value = results.forExpression("CountTestTime").value();
        assertThat(value, is(3));

        value = results.forExpression("CountTestNull").value();
        assertThat(value, is(0));

        value = results.forExpression("MaxTestInteger").value();
        assertThat(value, is(90));

        value = results.forExpression("MaxTestLong").value();
        assertThat(value, is(90L));

        value = results.forExpression("MaxTestString").value();
        assertThat(value, is("zebra"));

        value = results.forExpression("MaxTestDateTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 10, 6)));

        value = results.forExpression("MaxTestTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(20, 59, 59, 999)));

        value = results.forExpression("MedianTestDecimal").value();
        assertThat(value, is(new BigDecimal("3.5")));

        value = results.forExpression("MinTestInteger").value();
        assertThat(value, is(0));

        value = results.forExpression("MinTestLong").value();
        assertThat(value, is(0L));

        value = results.forExpression("MinTestString").value();
        assertThat(value, is("bye"));

        value = results.forExpression("MinTestDateTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 9, 5)));

        value = results.forExpression("MinTestTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(5, 59, 59, 999)));

        value = results.forExpression("ModeTestInteger").value();
        assertThat(value, is(9));
        value = results.forExpression("ModeTestDateTime2").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));

        value = results.forExpression("ModeTestTime").value();
        assertTrue(EquivalentEvaluator.equivalent(value, new Time(5, 59, 59, 999)));

        value = results.forExpression("ModeTestDateTime").value();
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 9, 5)));

        value = results.forExpression("PopStdDevTest1").value();
        assertEquals(
                0,
                ((BigDecimal) value)
                        .compareTo(new BigDecimal("1.41421356"))); // 23730951454746218587388284504413604736328125

        value = results.forExpression("PopulationStdDevIsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("PopVarianceTest1").value();
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal("2.0")));

        value = results.forExpression("PopVarianceIsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("StdDevTest1").value();
        assertEquals(
                0,
                ((BigDecimal) value)
                        .compareTo(new BigDecimal("1.58113883"))); // 00841897613935316257993690669536590576171875

        value = results.forExpression("StdDevIsNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SumTest1").value();
        assertThat(value, is(new BigDecimal("20.0")));

        value = results.forExpression("SumTestLong").value();
        assertThat(value, is(20L));

        value = results.forExpression("SumTestQuantity").value();
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal("15.0")).withUnit("ml")));

        value = results.forExpression("SumTestNull").value();
        assertThat(value, is(1));

        value = results.forExpression("VarianceTest1").value();
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal("2.5")));
    }
}
