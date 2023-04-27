package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;

public class IncludedSignatureWithAliasOutputTests extends CqlExecutionTestBase {

    // For this test we're using the minimum signature level that's supported, rather than the max
    @Override
    protected SignatureLevel getSignatureLevel() {
        return SignatureLevel.Overloads;
    }

    @Test
    public void testEvaluate() {
        Context context = new Context(library);
        context.registerLibraryLoader(new TestLibraryLoader(getLibraryManager(), this.translatorOptions()));

        Object result = context.resolveExpressionRef("TestOverload").getExpression().evaluate(context);
        assertThat(result, is(5));

        result = context.resolveExpressionRef("TestOverloadOneInt").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestOverloadOneDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("TestOverloadTwoInts").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("TestOverloadTwoDecimals").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("TestOverloadOneIntOneDecimal").getExpression().evaluate(context);
        assertThat(result, is(new BigDecimal("2.0")));

        result = context.resolveExpressionRef("TestOverloadOneIntTwoDecimal").getExpression().evaluate(context);
        assertThat(result, is(1));
    }
}