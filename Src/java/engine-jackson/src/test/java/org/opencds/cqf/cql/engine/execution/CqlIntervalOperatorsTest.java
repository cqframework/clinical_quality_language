package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.util.List;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;


public class CqlIntervalOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.AfterEvaluator#evaluate(Context)}
     */
    @Test
    public void TestAfter() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestAfterNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IntegerIntervalPointAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalPointAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IntegerIntervalAfterPointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalAfterPointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalPointAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalPointAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalAfterPointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalAfterPointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalPointAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalPointAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalAfterPointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalAfterPointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.BeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void TestBefore() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestBeforeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IntegerIntervalBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalPointBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalPointBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IntegerIntervalBeforePointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalBeforePointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalPointBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalPointBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalBeforePointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalBeforePointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalPointBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalPointBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalBeforePointTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalBeforePointFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.CollapseEvaluator#evaluate(Context)}
     */
    @Test
    public void TestCollapse() {
        Context context = new Context(library);
        Object result;
        result = context.resolveExpressionRef("TestCollapseNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalCollapse").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 10, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(12, true, 19, true)));

        result = context.resolveExpressionRef("IntegerIntervalCollapse2").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(1, true, 19, true)));

        result = context.resolveExpressionRef("IntegerIntervalCollapse3").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 8, true)));

        result = context.resolveExpressionRef("IntegerIntervalCollapse4").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(4, true, 6, true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(8, true, 10, true)));

        result = context.resolveExpressionRef("DecimalIntervalCollapse").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new BigDecimal("12.0"), true, new BigDecimal("19.0"), true)));

        result = context.resolveExpressionRef("DecimalIntervalCollapse2").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("8.0"), true)));

        result = context.resolveExpressionRef("QuantityIntervalCollapse").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)((List<?>) result).get(0)).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));
        Assert.assertTrue(((Interval)((List<?>) result).get(1)).equal(new Interval(new Quantity().withValue(new BigDecimal("12.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("19.0")).withUnit("g"), true)));

        result = context.resolveExpressionRef("DateTimeCollapse").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2012, 1, 25)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(null, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(null, 2012, 5, 30)));
        assertThat(((List<?>)result).size(), is(2));

        result = context.resolveExpressionRef("DateTimeCollapse2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2012, 5, 25)));
        assertThat(((List<?>)result).size(), is(1));

        result = context.resolveExpressionRef("DateTimeCollapse3").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new DateTime(null, 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new DateTime(null, 2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new DateTime(null, 2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new DateTime(null, 2018, 10, 15)));
        assertThat(((List<?>)result).size(), is(2));

        result = context.resolveExpressionRef("DateTimeCollapse4").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = context.resolveExpressionRef("DateTimeCollapse5").getExpression().evaluate(context);
        System.out.println(result);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 8, 28)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Date(2018, 8, 30)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Date(2018, 10, 15)));

        result = context.resolveExpressionRef("DateTimeCollapse6").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Date( 2018, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Date(2018, 10, 15)));


        result = context.resolveExpressionRef("TimeCollapse").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getStart(), new Time(17, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(1)).getEnd(), new Time(22, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = context.resolveExpressionRef("TimeCollapse2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getStart(), new Time(1, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)((List<?>)result).get(0)).getEnd(), new Time(15, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ContainsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestContains() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestContainsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TestNullElement1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestNullElement2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestNullElementTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

//        result = context.resolveExpressionRef("DateTimeContainsNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

//        result = context.resolveExpressionRef("TimeContainsNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TimeContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EndsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestEnds() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestEndsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalEndsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalEndsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalEndsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalEndsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalEndsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalEndsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeEndsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeEndsNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeEndsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeEndsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeEndsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator#evaluate(Context)}
     */
    @Test
    public void TestEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestEqualNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ExceptEvaluator#evaluate(Context)}
     */
    @Test
    public void TestExcept() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestExceptNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalExcept1to3").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 3, true)));

        result = context.resolveExpressionRef("IntegerIntervalExcept4to6").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(-4, false, 6, false)));

        result = context.resolveExpressionRef("IntegerIntervalExceptNullOutNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalExceptNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DecimalIntervalExcept1to3").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("3.99999999"), true)));

        result = context.resolveExpressionRef("DecimalIntervalExceptNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("QuantityIntervalExcept1to4").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("4.99999999")).withUnit("g"), true)));

        result = context.resolveExpressionRef("Except12").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 2, true)));

        result = context.resolveExpressionRef("ExceptDateTimeInterval").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 6)));

        result = context.resolveExpressionRef("ExceptDateTime2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 13)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 16)));

        result = context.resolveExpressionRef("ExceptTimeInterval").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(8, 59, 59, 998)));

        result = context.resolveExpressionRef("ExceptTime2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(11, 0, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(11, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.InEvaluator#evaluate(Context)}
     */
    @Test
    public void TestIn() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestInNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestInNullEnd").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestNullIn").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeInNullPrecision").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeInNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeInNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Issue32Interval").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IncludesEvaluator#evaluate(Context)}
     */
    @Test
    public void TestIncludes() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestIncludesNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IncludedInEvaluator#evaluate(Context)}
     */
    @Test
    public void TestIncludedIn() {
        Context context = new Context(library);

        // This is going to the InEvaluator for some reason
        // result = context.resolveExpressionRef("TestIncludedInNull").getExpression().evaluate(context);
        // assertThat(result, is(nullValue()));

        Object result = context.resolveExpressionRef("IntegerIntervalIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeIncludedInNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeIncludedInPrecisionTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeIncludedInPrecisionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IntersectEvaluator#evaluate(Context)}
     */
    @Test
    public void TestIntersect() {
        Context context = new Context(library);

        // result = context.resolveExpressionRef("TestIntersectNull").getExpression().evaluate(context);
        // assertThat(result, is(nullValue()));

        Object result = context.resolveExpressionRef("IntegerIntervalIntersectTest4to10").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(4, true, 10, true)));

        result = context.resolveExpressionRef("IntegerIntervalIntersectTestNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DecimalIntervalIntersectTest4to10").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("4.0"), true, new BigDecimal("10.0"), true)));

        result = context.resolveExpressionRef("IntegerIntervalIntersectTestNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("QuantityIntervalIntersectTest5to10").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));

        result = context.resolveExpressionRef("QuantityIntervalIntersectTestNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeIntersect").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 7)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 10)));

        result = context.resolveExpressionRef("TimeIntersect").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(4, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(6, 59, 59, 999)));

    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator#evaluate(Context)}
     */
    @Test
    public void TestEquivalent() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeEquivalentTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeEquivalentFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MeetsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestMeets() {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef("TestMeetsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalMeetsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalMeetsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalMeetsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalMeetsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalMeetsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalMeetsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeMeetsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeMeetsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeMeetsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MeetsBeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void TestMeetsBefore() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestMeetsBeforeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalMeetsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalMeetsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalMeetsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalMeetsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalMeetsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalMeetsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeMeetsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsBeforeNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeMeetsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeMeetsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.MeetsAfterEvaluator#evaluate(Context)}
     */
    @Test
    public void TestMeetsAfter() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestMeetsAfterNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalMeetsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalMeetsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalMeetsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalMeetsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalMeetsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalMeetsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeMeetsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsAfterNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeMeetsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeMeetsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeMeetsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.NotEqualEvaluator#evaluate(Context)}
     */
    @Test
    public void TestNotEqual() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalNotEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalNotEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalNotEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalNotEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalNotEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalNotEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeNotEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeNotEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeNotEqualTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeNotEqualFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrAfterEvaluator#evaluate(Context)}
     */
    @Test
    public void TestOnOrAfter() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestOnOrAfterNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TestOnOrAfterDateTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrAfterDateFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrAfterTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrAfterTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrAfterIntegerTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrAfterDecimalFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrAfterQuantityTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SameOrBeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void TestOnOrBefore() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestOnOrBeforeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TestOnOrBeforeDateTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrBeforeDateFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrBeforeTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrBeforeTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrBeforeIntegerTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TestOnOrBeforeDecimalFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TestOnOrBeforeQuantityTrue").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.OverlapsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestOverlaps() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestOverlapsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeOverlapsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeOverlapsNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeOverlapsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeOverlapsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeOverlapsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.OverlapsBeforeEvaluator#evaluate(Context)}
     */
    @Test
    public void TestOverlapsBefore() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestOverlapsBeforeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeOverlapsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeOverlapsBeforeNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeOverlapsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeOverlapsBeforeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeOverlapsBeforeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.OverlapsAfterEvaluator#evaluate(Context)}
     */
    @Test
    public void TestOverlapsAfter() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestOverlapsAfterNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalOverlapsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalOverlapsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalOverlapsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeOverlapsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeOverlapsAfterNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeOverlapsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeOverlapsAfterTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeOverlapsAfterFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.PointFromEvaluator#evaluate(Context)}
     */
    @Test
    public void TestPointFrom() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("TestPointFromNull").getExpression().evaluate(context);
        Assert.assertTrue(result == null);

        result = context.resolveExpressionRef("TestPointFromInteger").getExpression().evaluate(context);
        Assert.assertTrue((Integer) result == 1);

        result = context.resolveExpressionRef("TestPointFromDecimal").getExpression().evaluate(context);
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.0")) == 0);

        result = context.resolveExpressionRef("TestPointFromQuantity").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("cm")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ProperIncludesEvaluator#evaluate(Context)}
     */
    @Test
    public void TestProperlyIncludes() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestProperlyIncludesNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalProperlyIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalProperlyIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalProperlyIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalProperlyIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalProperlyIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalProperlyIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeProperlyIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeProperlyIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperlyIncludesTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperlyIncludesFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ProperContainsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestProperContains() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TimeProperContainsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperContainsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperContainsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TimeProperContainsPrecisionTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperContainsPrecisionFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperContainsPrecisionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ProperInEvaluator#evaluate(Context)}
     */
    @Test
    public void TestProperIn() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TimeProperInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperInNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TimeProperInPrecisionTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperInPrecisionFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperInPrecisionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.ProperIncludedInEvaluator#evaluate(Context)}
     */
    @Test
    public void TestProperlyIncludedIn() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestProperlyIncludedInNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalProperlyIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalProperlyIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalProperlyIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalProperlyIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalProperlyIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalProperlyIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeProperlyIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DateTimeProperlyIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeProperlyIncludedInTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeProperlyIncludedInFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.IntervalEvaluator#evaluate(Context)}
     */
    @Test
    public void TestInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalTest").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 10, true)));

        result = context.resolveExpressionRef("DecimalIntervalTest").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("10.0"), true)));

        result = context.resolveExpressionRef("QuantityIntervalTest").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("10.0")).withUnit("g"), true)));

        result = context.resolveExpressionRef("DateTimeIntervalTest").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2016, 5, 1, 0, 0, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2016, 5, 2, 0, 0, 0, 0)));

        result = context.resolveExpressionRef("TimeIntervalTest").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(0, 0, 0, 0)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(23, 59, 59, 599)));

        try {
            result = context.resolveExpressionRef("InvalidIntegerInterval").getExpression().evaluate(context);
            Assert.fail();
        } catch (RuntimeException re) {
            // pass
        }

        try {
            result = context.resolveExpressionRef("InvalidIntegerIntervalA").getExpression().evaluate(context);
            Assert.fail();
        } catch (RuntimeException re) {
            // pass
        }

        result = context.resolveExpressionRef("NullStartInterval").getExpression().evaluate(context);
        Assert.assertTrue(((Interval) result).getStart() == null);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval) result).getEnd(), new DateTime(null, 2018, 6, 15)));

        result = context.resolveExpressionRef("NullEndInterval").getExpression().evaluate(context);
        Assert.assertTrue(((Interval) result).getEnd() == null);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval) result).getStart(), new DateTime(null, 2018, 6, 15)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.SizeEvaluator#evaluate(Context)}
     */
    @Test
    public void TestSize() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("SizeTest").getExpression().evaluate(context);
        assertThat(result, is(5));

        result = context.resolveExpressionRef("SizeTestEquivalent").getExpression().evaluate(context);
        assertThat(result, is(5));

        result = context.resolveExpressionRef("SizeIsNull").getExpression().evaluate(context);
        Assert.assertNull(result);
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.StartEvaluator#evaluate(Context)}
     */
    @Test
    public void TestStart() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalStart").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("DecimalIntervalStart").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("QuantityIntervalStart").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g")));

        result = context.resolveExpressionRef("DateTimeIntervalStart").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 1, 0, 0, 0, 0)));

        result = context.resolveExpressionRef("TimeIntervalStart").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(0, 0, 0, 0)));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.StartsEvaluator#evaluate(Context)}
     */
    @Test
    public void TestStarts() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestStartsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalStartsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IntegerIntervalStartsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DecimalIntervalStartsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("DecimalIntervalStartsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("QuantityIntervalStartsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("QuantityIntervalStartsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("DateTimeStartsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef("DateTimeStartsNull").getExpression().evaluate(context);
//        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeStartsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("TimeStartsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("TimeStartsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.UnionEvaluator#evaluate(Context)}
     */
    @Test
    public void TestUnion() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IntegerIntervalUnion1To15").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(1, true, 15, true)));

        result = context.resolveExpressionRef("IntegerIntervalUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DecimalIntervalUnion1To15").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new BigDecimal("1.0"), true, new BigDecimal("15.0"), true)));

        result = context.resolveExpressionRef("DecimalIntervalUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("QuantityIntervalUnion1To15").getExpression().evaluate(context);
        Assert.assertTrue(((Interval)result).equal(new Interval(new Quantity().withValue(new BigDecimal("1.0")).withUnit("g"), true, new Quantity().withValue(new BigDecimal("15.0")).withUnit("g"), true)));

        result = context.resolveExpressionRef("QuantityIntervalUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DateTimeUnion").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new DateTime(null, 2012, 1, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new DateTime(null, 2012, 1, 28)));

        result = context.resolveExpressionRef("DateTimeUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TimeUnion").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getStart(), new Time(5, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((Interval)result).getEnd(), new Time(20, 59, 59, 999)));

        result = context.resolveExpressionRef("TimeUnionNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.WidthEvaluator#evaluate(Context)}
     */
    @Test
    public void TestWidth() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalTestWidth9").getExpression().evaluate(context);
        assertThat(result, is(9));

        result = context.resolveExpressionRef("IntervalTestWidthNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("DecimalIntervalTestWidth11").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("11.0")));

        result = context.resolveExpressionRef("QuantityIntervalTestWidth5").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.0")).withUnit("g")));

//        result = context.resolveExpressionRef("DateTimeWidth").getExpression().evaluate(context);
//        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("20")).withUnit("days")));
//
//        result = context.resolveExpressionRef("TimeWidth").getExpression().evaluate(context);
//        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("36000000")).withUnit("milliseconds")));
    }

    /**
     * {@link org.opencds.cqf.cql.engine.elm.execution.EndEvaluator#evaluate(Context)}
     */
    @Test
    public void TestEnd() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntegerIntervalEnd").getExpression().evaluate(context);
        assertThat(result, is(10));

        result = context.resolveExpressionRef("DecimalIntervalEnd").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("10.0")));

        result = context.resolveExpressionRef("QuantityIntervalEnd").getExpression().evaluate(context);
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10.0")).withUnit("g")));

        result = context.resolveExpressionRef("DateTimeIntervalEnd").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 2, 0, 0, 0, 0)));

        result = context.resolveExpressionRef("TimeIntervalEnd").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(23, 59, 59, 599)));
    }
}
