package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlAggregateFunctionsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlAggregateFunctionsTest.class;
    }

    @Test
    public void testAllTrue() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AllTrueList").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    @Test
    public void testAnyTrue() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AnyTrueList").getExpression().evaluate(context);
        assertThat(result, is(true));
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
