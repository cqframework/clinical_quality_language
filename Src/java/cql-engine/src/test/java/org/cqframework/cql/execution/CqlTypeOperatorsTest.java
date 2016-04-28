package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypeOperatorsTest {
    @Test
    public void testToTime() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(getClass().getResourceAsStream("TypeOperators.xml"), Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "StringToTime").getExpression().evaluate(context);
        assertThat(result, is("2:30PM UTC"));
    }
}
