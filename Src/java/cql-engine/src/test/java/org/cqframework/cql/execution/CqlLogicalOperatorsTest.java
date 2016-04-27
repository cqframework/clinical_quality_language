package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlLogicalOperatorsTest {
    @Test
    public void testAnd() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(getClass().getResourceAsStream("LogicalOperators.xml"), Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsAlsoFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef(library, "IsAlsoNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }
}
