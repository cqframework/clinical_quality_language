package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypesTest extends CqlExecutionTestBase {

    @Test
    public void testAny() throws JAXBException {
    }

    @Test
    public void testBoolean() throws JAXBException {
    }

    @Test
    public void testCode() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "CodeLiteral").getExpression().evaluate(context);
        assertThat(result, is(new org.cqframework.cql.runtime.Code().withCode("8480-6").withSystem("http://loinc.org").withDisplay("Systolic blood pressure")));
    }

    @Test
    public void testConcept() throws JAXBException {
    }

    @Test
    public void testDateTime() throws JAXBException {
    }

    @Test
    public void testDecimal() throws JAXBException {
    }

    @Test
    public void testInteger() throws JAXBException {
    }

    @Test
    public void testQuantity() throws JAXBException {
    }

    @Test
    public void testString() throws JAXBException {
    }

    @Test
    public void testTime() throws JAXBException {
    }
}
