package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlComparisonOperatorsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlComparisonOperatorsTest.class;
    }

    @Test
    public void testBetween() throws JAXBException {

    }

    @Test
    public void testEqual() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "SimpleAA").getExpression().evaluate(context);
        assertThat(result, is(true));
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
