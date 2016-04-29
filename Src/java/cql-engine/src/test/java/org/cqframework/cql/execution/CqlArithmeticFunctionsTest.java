package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlArithmeticFunctionsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlArithmeticFunctionsTest.class;
    }

    @Test
    public void testAbs() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "AllTrueList").getExpression().evaluate(context);
        assertThat(result, is(true));
    }

    @Test
    public void testCeiling() throws JAXBException {

    }

    @Test
    public void testFloor() throws JAXBException {

    }

    @Test
    public void testExp() throws JAXBException {

    }

    @Test
    public void testLn() throws JAXBException {

    }

    @Test
    public void testTruncate() throws JAXBException {

    }

    @Test
    public void testLog() throws JAXBException {

    }

    @Test
    public void testRound() throws JAXBException {

    }
}
