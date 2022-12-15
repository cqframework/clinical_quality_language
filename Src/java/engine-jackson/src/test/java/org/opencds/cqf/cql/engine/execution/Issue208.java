package org.opencds.cqf.cql.engine.execution;

import java.util.List;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Issue208 extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("Let Test 1").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(0), 1));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(1), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(2), 3));

        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(0), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(1), 5));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(2), 6));

        result = context.resolveExpressionRef("Let Test 2").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(0), 1));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(1), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(2), 3));

        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(0), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(1), 5));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(2), 6));
    }
}
