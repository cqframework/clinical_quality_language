package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Test(groups = {"a"})
public class CqlAggregateFunctionsTest extends CqlExecutionTestBase {

    /**
     * {@link org.cqframework.cql.elm.execution.AllTrue#evaluate(Context)}
     */
    @Test
    public void testAllTrue() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "AllTrueAllTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AllTrueTrueFirst").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AllTrueFalseFirst").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AllTrueAllTrueFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AllTrueAllFalseTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AllTrueNullFirst").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AllTrueEmptyList").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.AnyTrue#evaluate(Context)}
     */
    @Test
    public void testAnyTrue() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "AnyTrueNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "AnyTrueEmptyList").getExpression().evaluate(context);
        assertThat(result, is(false));

        //TODO: Figure out why this is throwing from AliasRef.evaluate.
//        result = context.resolveExpressionRef(library, "AnyTrueNullList").getExpression().evaluate(context);
//        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AnyTrueAllTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueAllFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "AnyTrueAllTrueFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueAllFalseTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueTrueFirst").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueFalseFirst").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueNullFirstThenTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "AnyTrueNullFirstThenFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Avg#evaluate(Context)}
     */
    @Test
    public void testAvg() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Avg12Null3").getExpression().evaluate(context);
        assertThat(result, is(2.0));

        result = context.resolveExpressionRef(library, "Avg22").getExpression().evaluate(context);
        assertThat(result, is(2.0));

        result = context.resolveExpressionRef(library, "Avg12").getExpression().evaluate(context);
        assertThat(result, is(1.5));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Count#evaluate(Context)}
     */
    @Test
    public void testCount() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "CountEmptyList").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef(library, "CountNullNull").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef(library, "Count12Null3").getExpression().evaluate(context);
        assertThat(result, is(3));

        result = context.resolveExpressionRef(library, "Count22").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef(library, "CountAB").getExpression().evaluate(context);
        assertThat(result, is(2));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Max#evaluate(Context)}
     */
    @Test
    public void testMax() throws JAXBException {
        Context context = new Context(library);
        Object result;

        result = context.resolveExpressionRef(library, "Max12Null3").getExpression().evaluate(context);
        assertThat(result, is(3.0));

        result = context.resolveExpressionRef(library, "Max12").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef(library, "MaxAB").getExpression().evaluate(context);
        assertThat(result, is("b"));

        //TODO: Update these once Quantity compare has been implemented.
//        result = context.resolveExpressionRef(library, "Max1CM2CM").getExpression().evaluate(context);
//        assertThat(result, is("b"));
//
//        result = context.resolveExpressionRef(library, "Max1M2CM").getExpression().evaluate(context);
//        assertThat(result, is("b"));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Median#evaluate(Context)}
     */
    @Test
    public void testMedian() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Min#evaluate(Context)}
     */
    @Test
    public void testMin() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Mode#evaluate(Context)}
     */
    @Test
    public void testMode() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.StdDev#evaluate(Context)}
     */
    @Test
    public void testPopulationStdDev() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.PopulationVariance#evaluate(Context)}
     */
    @Test
    public void testPopulationVariance() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.StdDev#evaluate(Context)}
     */
    @Test
    public void testStdDev() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Sum#evaluate(Context)}
     */
    @Test
    public void testSum() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Variance#evaluate(Context)}
     */
    @Test
    public void testVariance() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }
}
