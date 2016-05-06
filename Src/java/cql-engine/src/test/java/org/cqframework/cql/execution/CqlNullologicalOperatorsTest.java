package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlNullologicalOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.cqframework.cql.elm.execution.Coalesce#evaluate(Context)}
     */
    @Test
    public void testCoalesce() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "CoalesceEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CoalesceNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CoalesceA").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CoalesceANull").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef(library, "CoalesceNullA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef(library, "CoalesceEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CoalesceListFirstA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef(library, "CoalesceListLastA").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef(library, "CoalesceFirstList").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a")));

        result = context.resolveExpressionRef(library, "CoalesceLastList").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.IsNull#evaluate(Context)}
     */
    @Test
    public void testIsNull() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsNullFalseEmptyString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseAbcString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumber1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumberZero").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.IsFalse#evaluate(Context)}
     */
    @Test
    public void testIsFalse() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsFalseNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.IsTrue#evaluate(Context)}
     */
    @Test
    public void testIsTrue() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsTrueNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }
}
