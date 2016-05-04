package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlComparisonOperatorsTest extends CqlExecutionTestBase {

    @Test
    public void testBetween() throws JAXBException {
        //TODO: This seems to be missing from org.cqframework.cql.elm.execution;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Equal#evaluate(Context)}
     */
    @Test
    public void testEqual() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "SimpleEqTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleEqFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleEqNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleEqTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleEqNullTrue").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleEqInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleEqStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleEqFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleEqFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleEqFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "QuantityEqCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "QuantityEqCM1M01").getExpression().evaluate(context);
        assertThat(result, is(false));

        //TODO: Uncomment once Tuple evaluate has been implemented.
//        result = context.resolveExpressionRef(library, "TupleEqJohnJohn").getExpression().evaluate(context);
//        assertThat(result, is(true));
//
//        result = context.resolveExpressionRef(library, "TupleEqJohnJane").getExpression().evaluate(context);
//        assertThat(result, is(false));
//
//        result = context.resolveExpressionRef(library, "TupleEqJohn1John2").getExpression().evaluate(context);
//        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListEqEmptyEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListEqABCABC").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListEqABCAB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListEqABC123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListEq123ABC").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListEq123String123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IntervalEq1To101To10").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IntervalEq1To101To5").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "DateTimeEqTodayToday").getExpression().evaluate(context);
        assertThat(result, is(true));

        //TODO: Won't pass because date arithmatic not completed.
//        result = context.resolveExpressionRef(library, "DateTimeEqTodayYesterday").getExpression().evaluate(context);
//        assertThat(result, is(false));

        //TODO: Won't pass because of FunctionRef evaluate not implemented
//        result = context.resolveExpressionRef(library, "TimeEq10A10A").getExpression().evaluate(context);
//        assertThat(result, is(true));
//
//        result = context.resolveExpressionRef(library, "TimeEq10A10P").getExpression().evaluate(context);
//        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Greater#evaluate(Context)}
     */
    @Test
    public void testGreater() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "GreaterZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "GreaterDecZZ").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterDecZ1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterDecZNeg1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "GreaterCM0CM0").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterCM0CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "GreaterCM0NegCM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "QuantityEqM1CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "QuantityEqM1CM10").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.GreaterOrEqual#evaluate(Context)}
     */
    @Test
    public void testGreaterOrEqual() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Less#evaluate(Context)}
     */
    @Test
    public void testLess() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.LessOrEqual#evaluate(Context)}
     */
    @Test
    public void testLessOrEqual() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Equivalent#evaluate(Context)}
     */
    @Test
    public void testEquivalent() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.NotEqual#evaluate(Context)}
     */
    @Test
    public void testNotEqual() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "SimpleNotEqTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleNotEqFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleNotEqNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleNotEqTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleNotEqNullTrue").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleNotEqInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleNotEqStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleNotEqFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleNotEqFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNotEqFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "QuantityNotEqCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "QuantityNotEqCM1M01").getExpression().evaluate(context);
        assertThat(result, is(true));

        //TODO: Uncomment once Tuple evaluate has been implemented.
//        result = context.resolveExpressionRef(library, "TupleNotEqJohnJohn").getExpression().evaluate(context);
//        assertThat(result, is(false));
//
//        result = context.resolveExpressionRef(library, "TupleNotEqJohnJane").getExpression().evaluate(context);
//        assertThat(result, is(true));
//
//        result = context.resolveExpressionRef(library, "TupleNotEqJohn1John2").getExpression().evaluate(context);
//        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListNotEqEmptyEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListNotEqABCABC").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ListNotEqABCAB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListNotEqABC123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListNotEq123ABC").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "ListNotEq123String123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IntervalNotEq1To101To10").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IntervalNotEq1To101To5").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "DateTimeNotEqTodayToday").getExpression().evaluate(context);
        assertThat(result, is(false));

        //TODO: Won't pass because date arithmatic not completed.
//        result = context.resolveExpressionRef(library, "DateTimeNotEqTodayYesterday").getExpression().evaluate(context);
//        assertThat(result, is(true));

        //TODO: Won't pass because of FunctionRef evaluate not implemented
//        result = context.resolveExpressionRef(library, "TimeNotEq10A10A").getExpression().evaluate(context);
//        assertThat(result, is(false));
//
//        result = context.resolveExpressionRef(library, "TimeNotEq10A10P").getExpression().evaluate(context);
//        assertThat(result, is(true));
    }
}
