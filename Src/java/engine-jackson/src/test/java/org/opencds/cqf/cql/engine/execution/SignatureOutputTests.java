package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;

public class SignatureOutputTests extends CqlExecutionTestBase {

    // For this test we're using the minimum signature level that's supported, rather than the max
    @Override
    protected SignatureLevel getSignatureLevel() {
        return SignatureLevel.Overloads;
    }

    @Test
    public void testEvaluate() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TestIntegerOverload").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("1")));

        result = context.resolveExpressionRef("TestDecimalOverload").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("1.0")));

        result = context.resolveExpressionRef("TestMultipleOverload").getExpression().evaluate(context);
        assertThat(result, is(5));

        result = context.resolveExpressionRef("TestIntegerMultipleOverload").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestDecimalMultipleOverload").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("TestIntegerAndDecimalMultipleOverload").getExpression().evaluate(context);
        assertThat(result, is(1));
    }
}