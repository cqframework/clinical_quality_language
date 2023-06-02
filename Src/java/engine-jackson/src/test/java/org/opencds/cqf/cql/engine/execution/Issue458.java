package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

public class Issue458 extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Closed-Open Interval").getExpression().evaluate(context);
        Interval interval = (Interval)result;
        assertEquals(interval.toString(), "Interval[3, 5)");
    }
}