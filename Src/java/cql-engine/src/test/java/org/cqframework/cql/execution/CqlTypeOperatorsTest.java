package org.cqframework.cql.execution;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypeOperatorsTest  extends CqlExecutionTestBase {
    static {
        testClass = CqlTypeOperatorsTest.class;
    }

    @Test
    public void testToTime() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "StringToTime").getExpression().evaluate(context);
        assertThat(result, is("2:30PM UTC"));
    }
}
