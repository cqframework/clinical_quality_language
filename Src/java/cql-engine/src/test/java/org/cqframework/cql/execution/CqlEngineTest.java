package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by Bryn on 4/12/2016.
 */
public class CqlEngineTest {

    @Test
    public void testMath() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(getClass().getResourceAsStream("Math.xml"), Library.class);

        // TODO: The matcher here uses .equal, needs to use .compareTo == 0 for BigDecimals...
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "Add").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("15.0")));

        result = context.resolveExpressionRef(library, "Subtract").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("5.0")));

        result = context.resolveExpressionRef(library, "Multiply").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("50.00")));

        result = context.resolveExpressionRef(library, "Divide").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2")));
    }
}
