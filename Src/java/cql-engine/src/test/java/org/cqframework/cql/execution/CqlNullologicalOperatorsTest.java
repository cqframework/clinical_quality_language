package org.cqframework.cql.execution;

import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlNullologicalOperatorsTest extends CqlExecutionTestBase {
    static {
        testClass = CqlNullologicalOperatorsTest.class;
    }

    @Test
    public void testIsNull() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsNullFalseEmptyString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseAbcString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumber1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumberZero").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    @Test
    public void testIsFalse() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsFalseNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    @Test
    public void testIsTrue() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsTrueNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }
}
