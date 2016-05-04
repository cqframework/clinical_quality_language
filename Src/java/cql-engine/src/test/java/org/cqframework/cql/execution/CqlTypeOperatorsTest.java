package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

public class CqlTypeOperatorsTest extends CqlExecutionTestBase {

    /**
     * {@link org.cqframework.cql.elm.execution.As#evaluate(Context)}
     */
    @Test
    public void testAS() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "Int1ToString").getExpression().evaluate(context);
        //assertThat(result, is("2:30PM UTC"));
    }

    /**
     * {@link org.cqframework.cql.elm.execution.Convert#evaluate(Context)}
     */
    @Test
    public void testConvert() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.Is#evaluate(Context)}
     */
    @Test
    public void testIs() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToBoolean#evaluate(Context)}
     */
    @Test
    public void testToBoolean() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToConcept#evaluate(Context)}
     */
    @Test
    public void testToConcept() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToDateTime#evaluate(Context)}
     */
    @Test
    public void testToDateTime() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToDecimal#evaluate(Context)}
     */
    @Test
    public void testToDecimal() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToInteger#evaluate(Context)}
     */
    @Test
    public void testToInteger() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToQuantity#evaluate(Context)}
     */
    @Test
    public void testToQuantity() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToString#evaluate(Context)}
     */
    @Test
    public void testToString() throws JAXBException {

    }

    /**
     * {@link org.cqframework.cql.elm.execution.ToTime#evaluate(Context)}
     */
    @Test
    public void testToTime() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "StringToTime").getExpression().evaluate(context);
        //assertThat(result, is("2:30PM UTC"));
    }
}
