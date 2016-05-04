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

    /**
     * {@link org.cqframework.cql.elm.execution.Code#evaluate(Context)}
     */
    @Test
    public void testCode() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "CodeLiteral").getExpression().evaluate(context);
        assertThat(result, is(new org.cqframework.cql.runtime.Code().withCode("8480-6").withSystem("http://loinc.org").withDisplay("Systolic blood pressure")));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Concept#evaluate(Context)}
     */
    @Test
    public void testConcept() throws JAXBException {
    }

    /**
     * {@link org.cqframework.cql.elm.execution.DateTime#evaluate(Context)}
     */
    @Test
    public void testDateTime() throws JAXBException {
    }

    @Test
    public void testDecimal() throws JAXBException {
    }

    @Test
    public void testInteger() throws JAXBException {
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Quantity#evaluate(Context)}
     */
    @Test
    public void testQuantity() throws JAXBException {
    }

    @Test
    public void testString() throws JAXBException {
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Time#evaluate(Context)}
     */
    @Test
    public void testTime() throws JAXBException {
    }
}
