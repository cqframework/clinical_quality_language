package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.testng.annotations.Test;

public class Issue223 extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Access Flattened List of List Items").getExpression().evaluate(context);
        List<?> list = (List<?>)result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));

        result = context.resolveExpressionRef("Access Flattened List of List Items in a Single Query").getExpression().evaluate(context);
        list = (List<?>)result;
        assertThat(list.size(), is(1));
        assertThat(list.get(0), is(true));
    }
}