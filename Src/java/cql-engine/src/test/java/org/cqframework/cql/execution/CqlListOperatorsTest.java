package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlListOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.cqframework.cql.elm.execution.Contains#evaluate(Context)}
     */
    @Test
    public void testContains() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "ContainsEmptyListHasNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ContainsABCHasA").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Distinct#evaluate(Context)}
     */
    @Test
    public void testDistinct() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "DistinctEmptyList").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList()));

        result = context.resolveExpressionRef(library, "DistinctNullNullNull").getExpression().evaluate(context);
        assertThat(result, is(new ArrayList<Object>() {{
            add(null);
        }}));

        result = context.resolveExpressionRef(library, "DistinctANullANull").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a", null)));

        result = context.resolveExpressionRef(library, "Distinct112233").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef(library, "Distinct123123").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef(library, "DistinctAABBCC").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = context.resolveExpressionRef(library, "DistinctABCABC").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a", "b", "c")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Equal#evaluate(Context)}
     */
    @Test
    public void testEqual() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "EqualNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "EqualEmptyListNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "EqualNullEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "EqualEmptyListAndEmptyList").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "Equal12And123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "Equal123And12").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "Equal123And123").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Except#evaluate(Context)}
     */
    @Test
    public void testExcept() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "ExceptEmptyListAndEmptyList").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList()));

        result = context.resolveExpressionRef(library, "Except1234And23").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 4)));

        result = context.resolveExpressionRef(library, "Except23And1234").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList()));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Exists#evaluate(Context)}
     */
    @Test
    public void testExists() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "ExistsEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "ExistsListNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "Exists1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "Exists12").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Flatten#evaluate(Context)}
     */
    @Test
    public void testFlatten() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "FlattenEmpty").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList()));

        result = context.resolveExpressionRef(library, "FlattenListNullAndNull").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(null, null)));

        result = context.resolveExpressionRef(library, "FlattenList12And34").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.First#evaluate(Context)}
     */
    @Test
    public void testFirst() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "FirstEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "FirstNull1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "First1Null").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "First12").getExpression().evaluate(context);
        assertThat(result, is(1));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.In#evaluate(Context)}
     */
    @Test
    public void testIn() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "InNullEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "InNullAnd1Null").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "In1Null").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "In1And12").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "In3And12").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Includes#evaluate(Context)}
     */
    @Test
    public void testIncludes() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "IncludesEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IncludesListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "Includes123And2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "Includes123And4").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.IncludedIn#evaluate(Context)}
     */
    @Test
    public void testIncludedIn() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "IncludedInEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IncludedInListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IncludedIn2And123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IncludedIn4And123").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Indexer#evaluate(Context)}
     */
    @Test
    public void testIndexer() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "IndexerNull1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "Indexer0Of12").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "Indexer1Of12").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef(library, "Indexer2Of12").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexerNeg1Of12").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }


    /**
     * {@link org.cqframework.cql.elm.execution.IndexOf#evaluate(Context)}
     */
    @Test
    public void testIndexOf() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "IndexOfEmptyNull").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef(library, "IndexOfNullEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IndexOfNullIn1Null").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "IndexOf1In12").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef(library, "IndexOf2In12").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef(library, "IndexOf3In12").getExpression().evaluate(context);
        assertThat(result, is(-1));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Intersect#evaluate(Context)}
     */
    @Test
    public void testIntersect() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Last#evaluate(Context)}
     */
    @Test
    public void testLast() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Length#evaluate(Context)}
     */
    @Test
    public void testLength() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Equivalent#evaluate(Context)}
     */
    @Test
    public void testEquivalent() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.NotEqual#evaluate(Context)}
     */
    @Test
    public void testNotEqual() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    @Test
    public void testProperlyInclues() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    @Test
    public void testProperlyIncludedIn() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.SingletonFrom#evaluate(Context)}
     */
    @Test
    public void testSingletonFrom() throws JAXBException {
    }

    Context context = new Context(library);
    Object result;

    /**
     * {@link org.cqframework.cql.elm.execution.Union#evaluate(Context)}
     */
    @Test
    public void testUnion() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }
}
