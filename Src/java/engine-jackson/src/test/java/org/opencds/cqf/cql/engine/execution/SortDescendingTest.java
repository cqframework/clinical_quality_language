package org.opencds.cqf.cql.engine.execution;

import java.util.List;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SortDescendingTest extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("sorted list of numbers descending").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(0), 9));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(1), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(2), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(3), 1));
    }
}