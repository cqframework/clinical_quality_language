package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypeOperatorsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlTypeOperatorsTest.class;
    }

    @Test
    public void testAS() throws JAXBException {

    }

    @Test
    public void testConvert() throws JAXBException {

    }

    @Test
    public void testIs() throws JAXBException {

    }

    @Test
    public void testToBoolean() throws JAXBException {

    }

    @Test
    public void testToConcept() throws JAXBException {

    }

    @Test
    public void testToDateTime() throws JAXBException {

    }

    @Test
    public void testToDecimal() throws JAXBException {

    }

    @Test
    public void testToInteger() throws JAXBException {

    }

    @Test
    public void testToQuantity() throws JAXBException {

    }

    @Test
    public void testToString() throws JAXBException {
        
    }

    @Test
    public void testToTime() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "StringToTime").getExpression().evaluate(context);
        assertThat(result, is("2:30PM UTC"));
    }
}
