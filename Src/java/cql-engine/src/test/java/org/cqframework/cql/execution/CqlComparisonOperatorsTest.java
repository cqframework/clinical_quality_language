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

    @Test
    public void testEqual() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "SimpleTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleNullTrue").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "SimpleInt1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleInt1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleStringAStringA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleStringAStringB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleCharACharA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleCharACharB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleFloat1Float1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleFloat1Float2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "SimpleFloat1Int1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "SimpleFloat1Int2").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "QuantityCM1CM1").getExpression().evaluate(context);
        assertThat(result, is(true));

//        result = context.resolveExpressionRef(library, "QuantityCM1M01").getExpression().evaluate(context);
//        assertThat(result, is(true));
    }

    @Test
    public void testGreater() throws JAXBException {

    }

    @Test
    public void testGreaterOrEqual() throws JAXBException {

    }

    @Test
    public void testLess() throws JAXBException {

    }

    @Test
    public void testLessOrEqual() throws JAXBException {

    }

    @Test
    public void testEquivalent() throws JAXBException {

    }

    @Test
    public void testNotEqual() throws JAXBException {

    }
}
