package org.cqframework.cql.execution;

import org.cqframework.cql.runtime.Interval;
import org.cqframework.cql.runtime.Quantity;
import org.joda.time.Partial;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Bryn on 5/1/2016.
 */
public class CqlIntervalOperatorsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlIntervalOperatorsTest.class;
    }

    @Test
    public void TestIntervalOperators() {
        Context context = new Context(library);

        /*
        //Interval
        define IntegerIntervalTest: Interval[1, 10]
        define DecimalIntervalTest: Interval[1.0, 10.0]
        define QuantityIntervalTest: Interval[1.0 'g', 10.0 'g']
        //define DateTimeIntervalTest: Interval[@2016-05-01T00:00:00Z, @2016-05-02T00:00:00Z)
        //define TimeIntervalTest: Interval[@T00:00:00Z, @T23:59:59Z]
         */
        Object result = context.resolveExpressionRef(library, "IntegerIntervalTest").getExpression().evaluate(context);
        assertThat(result, is(new Interval(1, true, 10, true)));

        result = context.resolveExpressionRef(library, "DecimalIntervalTest").getExpression().evaluate(context);
        assertThat(result, is(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));

        result = context.resolveExpressionRef(library, "QuantityIntervalTest").getExpression().evaluate(context);
        assertThat(result, is(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));

        //result = context.resolveExpressionRef(library, "DateTimeIntervalTest").getExpression().evaluate(context);
        //assertThat(result, is(new Interval(new Partial("2016-05-01T00:00:00Z"), true, new Partial("2016-05-02T00:00:00Z", false))));

        //result = context.resolveExpressionRef(library, "TimeIntervalTest").getExpression().evaluate(context);
        //assertThat(result, is(new Interval(new PartialTime("T00:00:00Z"), true, new PartialTime("T23:59:59Z"), true)));

        /*
        //MinValue
        define IntegerMinValue: minimum Integer
        define DecimalMinValue: minimum Decimal
        define QuantityMinValue: minimum Quantity
        //define DateTimeMinValue: minimum DateTime
        //define TimeMinValue: minimum Time
         */
        result = context.resolveExpressionRef(library, "IntegerMinValue").getExpression().evaluate(context);
        assertThat(result, is(Integer.MIN_VALUE));

        result = context.resolveExpressionRef(library, "DecimalMinValue").getExpression().evaluate(context);
        assertThat(result, is(Interval.minValue(BigDecimal.class)));

        result = context.resolveExpressionRef(library, "QuantityMinValue").getExpression().evaluate(context);
        assertThat(result, is(Interval.minValue(Quantity.class)));

        //result = context.resolveExpressionRef(library, "DateTimeMinValue").getExpression().evaluate(context);
        //assertThat(result, is(Interval.minValue(Partial.class)));

        //result = context.resolveExpressionRef(library, "TimeMinValue").getExpression().evaluate(context);
        //assertThat(result, is(Interval.minValue(PartialTime.class)));

        /*
        //MaxValue
        define IntegerMaxValue: maximum Integer
        define DecimalMaxValue: maximum Decimal
        define QuantityMaxValue: maximum Quantity
        //define DateTimeMaxValue: maximum DateTime
        //define TimeMaxValue: maximum Time
        */
        result = context.resolveExpressionRef(library, "IntegerMaxValue").getExpression().evaluate(context);
        assertThat(result, is(Integer.MAX_VALUE));

        result = context.resolveExpressionRef(library, "DecimalMaxValue").getExpression().evaluate(context);
        assertThat(result, is(Interval.maxValue(BigDecimal.class)));

        result = context.resolveExpressionRef(library, "QuantityMaxValue").getExpression().evaluate(context);
        assertThat(result, is(Interval.maxValue(Quantity.class)));

        //result = context.resolveExpressionRef(library, "DateTimeMaxValue").getExpression().evaluate(context);
        //assertThat(result, is(Interval.maxValue(Partial.class)));

        //result = context.resolveExpressionRef(library, "TimeMaxValue").getExpression().evaluate(context);
        //assertThat(result, is(Interval.maxValue(PartialTime.class)));

        /*
        //Successor
        define IntegerSuccessor: successor of 1
        define DecimalSuccessor: successor of 1.0
        define QuantitySuccessor: successor of 1.0 'g'
        //define DateTimeSuccessor: successor of @2016-05-01T00:00:00Z
        //define TimeSuccessor: successor of @T00:00:00Z
         */
        result = context.resolveExpressionRef(library, "IntegerSuccessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.successor(1)));

        result = context.resolveExpressionRef(library, "DecimalSuccessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.successor(new BigDecimal(1.0))));

        result = context.resolveExpressionRef(library, "QuantitySuccessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.successor(new Quantity().withValue(new BigDecimal(1.0)).withUnit("g"))));

        //result = context.resolveExpressionRef(library, "DateTimeSuccessor").getExpression().evaluate(context);
        //assertThat(result, is(Interval.successor(new Partial("2016-05-01T00:00:00Z"))));

        //result = context.resolveExpressionRef(library, "TimeSuccessor").getExpression().evaluate(context);
        //assertThat(result, is(Interval.successor(new PartialTime("2016-05-01T00:00:00Z"))));

        /*
        //Predecessor
        define IntegerPredecessor: predecessor of 1
        define DecimalPredecessor: predecessor of 1.0
        define QuantityPredecessor: predecessor of 1.0 'g'
        //define DateTimePredecessor: predecessor of @2016-05-01T00:00:00Z
        //define TimePredecessor: predecessor of @T00:00:00Z
         */
        result = context.resolveExpressionRef(library, "IntegerPredecessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.predecessor(1)));

        result = context.resolveExpressionRef(library, "DecimalPredecessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.predecessor(new BigDecimal(1.0))));

        result = context.resolveExpressionRef(library, "QuantityPredecessor").getExpression().evaluate(context);
        assertThat(result, is(Interval.predecessor(new Quantity().withValue(new BigDecimal(1.0)).withUnit("g"))));

        //result = context.resolveExpressionRef(library, "DateTimePredecessor").getExpression().evaluate(context);
        //assertThat(result, is(Interval.predecessor(new Partial("2016-05-01T00:00:00Z"))));

        //result = context.resolveExpressionRef(library, "TimePredecessor").getExpression().evaluate(context);
        //assertThat(result, is(Interval.predecessor(new PartialTime("2016-05-01T00:00:00Z"))));

        /*
        //Start
        define IntegerIntervalStart: start of IntegerIntervalTest
        define DecimalIntervalStart: start of DecimalIntervalTest
        define QuantityIntervalStart: start of QuantityIntervalTest
        //define DateTimeIntervalStart: start of DateTimeIntervalTest
        //define TimeIntervalStart: start of TimeIntervalTest
         */
        result = context.resolveExpressionRef(library, "IntegerIntervalStart").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "DecimalIntervalStart").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("1.0")));

        result = context.resolveExpressionRef(library, "QuantityIntervalStart").getExpression().evaluate(context);
        assertThat(result, is(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g")));

        //result = context.resolveExpressionRef(library, "DateTimeIntervalStart").getExpression().evaluate(context);
        //assertThat(result, is(new Partial("@2016-05-01T00:00:00Z")));

        //result = context.resolveExpressionRef(library, "TimeIntervalStart").getExpression().evaluate(context);
        //assertThat(result, is(new PartialTime("T00:00:00Z")));

        /*
        //End
        define IntegerIntervalEnd: end of IntegerIntervalTest
        define DecimalIntervalEnd: end of DecimalIntervalTest
        define QuantityIntervalEnd: end of QuantityIntervalTest
        //define DateTimeIntervalEnd: end of DateTimeIntervalTest
        //define TimeIntervalEnd: end of TimeIntervalTest
         */
        result = context.resolveExpressionRef(library, "IntegerIntervalEnd").getExpression().evaluate(context);
        assertThat(result, is(10));

        result = context.resolveExpressionRef(library, "DecimalIntervalEnd").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("10.0")));

        result = context.resolveExpressionRef(library, "QuantityIntervalEnd").getExpression().evaluate(context);
        assertThat(result, is(new Quantity().withValue(new BigDecimal("10.0")).withUnit("g")));

        //result = context.resolveExpressionRef(library, "DateTimeIntervalEnd").getExpression().evaluate(context);
        //assertThat(result, is(Interval.predecessor(new Partial("@2016-05-02T00:00:00Z"))));

        //result = context.resolveExpressionRef(library, "TimeIntervalEnd").getExpression().evaluate(context);
        //assertThat(result, is(new PartialTime("T23:59:59Z")));
    }
}
