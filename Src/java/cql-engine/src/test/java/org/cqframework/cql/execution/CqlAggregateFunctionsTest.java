package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlAggregateFunctionsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlAggregateFunctionsTest.class;
    }

    @Test
    public void testAllTrue() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AllTrueAllTrue").getExpression().evaluate(context);
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
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testAnyTrue() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AnyTrueAllTrue").getExpression().evaluate(context);
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

        result = context.resolveExpressionRef(library, "AnyTrueEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    @Test
    public void testAvg() throws JAXBException {

    }

    @Test
    public void testCount() throws JAXBException {

    }

    @Test
    public void testMax() throws JAXBException {

    }

    @Test
    public void testMedian() throws JAXBException {

    }

    @Test
    public void testMin() throws JAXBException {

    }

    @Test
    public void testMode() throws JAXBException {

    }

    @Test
    public void testPopulationStdDev() throws JAXBException {

    }

    @Test
    public void testPopulationVariance() throws JAXBException {

    }

    @Test
    public void testStdDev() throws JAXBException {

    }

    @Test
    public void testSum() throws JAXBException {

    }


    @Test
    public void testVariance() throws JAXBException {

    }
}
