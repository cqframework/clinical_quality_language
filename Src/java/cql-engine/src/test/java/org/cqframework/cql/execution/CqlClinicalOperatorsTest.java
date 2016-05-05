package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

public class CqlClinicalOperatorsTest extends CqlExecutionTestBase {

    @Test
    public void testAge() throws JAXBException {
        //TODO: Does this have a corresponding elm execution?
    }

    @Test
    public void testAgeAt() throws JAXBException {
        //TODO: Does this have a corresponding elm execution?
    }

    /**
     * {@link org.cqframework.cql.elm.execution.CalculateAge#evaluate(Context)}
     */
    @Test
    public void testCalculateAge() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.CalculateAgeAt#evaluate(Context)}
     */
    @Test
    public void testCalculateAgeAt() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Equal#evaluate(Context)}
     */
    @Test
    public void testEqual() throws JAXBException {
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
     * {@link org.cqframework.cql.elm.execution.InCodeSystem#evaluate(Context)}
     */
    @Test
    public void testInCodesystem() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }

    /**
     * {@link org.cqframework.cql.elm.execution.ValueSetDef#evaluate(Context)}
     */
    @Test
    public void testInValueset() throws JAXBException {
        Context context = new Context(library);
        Object result;
    }
}
