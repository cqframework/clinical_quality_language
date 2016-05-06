package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlStringOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.cqframework.cql.elm.execution.Combine#evaluate(Context)}
     */
    @Test
    public void testCombine() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "CombineNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "CombineEmptyList").getExpression().evaluate(context);
        assertThat(result, is(""));

        result = context.resolveExpressionRef(library, "CombineABC").getExpression().evaluate(context);
        assertThat(result, is("abc"));

        result = context.resolveExpressionRef(library, "CombineABCSepDash").getExpression().evaluate(context);
        assertThat(result, is("a-b-c"));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Concatenate#evaluate(Context)}
     */
    @Test
    public void testConcatenate() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "ConcatenateNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "ConcatenateANull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "ConcatenateNullB").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "ConcatenateAB").getExpression().evaluate(context);
        assertThat(result, is("ab"));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Indexer#evaluate(Context)}
     */
    @Test
    public void testIndexer() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "IndexerNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexerANull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexerNull1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexerAB0").getExpression().evaluate(context);
        assertThat(result, is("a"));

        result = context.resolveExpressionRef(library, "IndexerAB1").getExpression().evaluate(context);
        assertThat(result, is("b"));

        result = context.resolveExpressionRef(library, "IndexerAB2").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexerABNeg1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Length#evaluate(Context)}
     */
    @Test
    public void testLength() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "LengthNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "LengthEmpty").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef(library, "LengthA").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "LengthAB").getExpression().evaluate(context);
        assertThat(result, is(2));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Lower#evaluate(Context)}
     */
    @Test
    public void testLower() throws JAXBException {
        Context context = new Context(library);
        Object result;

    }

    /**
     * {@link org.cqframework.cql.elm.execution.PositionOf#evaluate(Context)}
     */
    @Test
    public void testPositionOf() throws JAXBException {
        Context context = new Context(library);
        Object result;

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Split#evaluate(Context)}
     */
    @Test
    public void testSplit() throws JAXBException {
        Context context = new Context(library);
        Object result;

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Substring#evaluate(Context)}
     */
    @Test
    public void testSubstring() throws JAXBException {
        Context context = new Context(library);
        Object result;

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Upper#evaluate(Context)}
     */
    @Test
    public void testUpper() throws JAXBException {
        Context context = new Context(library);
        Object result;

    }
}
