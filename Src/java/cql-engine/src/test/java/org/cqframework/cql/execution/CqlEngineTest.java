package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Bryn on 4/12/2016.
 */
public class CqlEngineTest extends CqlExecutionTestBase {

    @Test
    public void testMath() throws JAXBException {
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
