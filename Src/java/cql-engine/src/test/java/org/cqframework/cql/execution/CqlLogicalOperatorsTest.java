package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlLogicalOperatorsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlLogicalOperatorsTest.class;
    }
    
    @Test
    public void testAnd() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AndTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AndFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AndAlsoFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AndNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "AndAlsoNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testNot() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "NotTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "NotFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "NotNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testOr() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "OrTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "OrAlsoTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "OrFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "OrNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "OrAlsoNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testXOr() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "XOrTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "XOrAlsoTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "XOrFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "XOrAlsoFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "XOrNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "XOrAlsoNull1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "XOrAlsoNull2").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }
}
