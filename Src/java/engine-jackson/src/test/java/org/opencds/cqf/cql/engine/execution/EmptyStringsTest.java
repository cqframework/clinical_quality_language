package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class EmptyStringsTest extends CqlExecutionTestBase {
    @Test
    public void testEmptyStrings() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Null").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Space").getExpression().evaluate(context);
        assertThat(result, is(" "));

        result = context.resolveExpressionRef("Empty").getExpression().evaluate(context);
        assertThat(result, is(""));
    }
}
