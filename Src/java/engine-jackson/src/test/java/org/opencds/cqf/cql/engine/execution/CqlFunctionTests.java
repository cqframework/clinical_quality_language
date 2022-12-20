package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class CqlFunctionTests extends CqlExecutionTestBase {

    @Test
    public void testFunction() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("FunctionTestStringArg").getExpression().evaluate(context);
        assertThat(result, is("hello"));

        result = context.resolveExpressionRef("FunctionTestNullStringArg").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("FunctionTestMultipleArgs").getExpression().evaluate(context);
        assertThat(result, is("hell0"));

        result = context.resolveExpressionRef("FunctionTestNullMultipleArgs").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("FunctionTestOverload").getExpression().evaluate(context);
        assertThat(result, is("hell00.000"));

        result = context.resolveExpressionRef("FunctionTestNullOverload").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("FunctionTestTupleArg").getExpression().evaluate(context);
        assertThat(result, is(3));

        result = context.resolveExpressionRef("FunctionTestNullTupleArg").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("FunctionTestQuantityArg").getExpression().evaluate(context);
        assertThat(result, is("cm"));

        result = context.resolveExpressionRef("FunctionTestNullQuantityArg").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }
}
