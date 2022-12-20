package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class Issue39 extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("EquivalentIntervals").getExpression().evaluate(context);
        assertThat(result, is(true));
    }
}
