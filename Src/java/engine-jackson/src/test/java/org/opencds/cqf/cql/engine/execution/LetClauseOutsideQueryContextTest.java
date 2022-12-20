package org.opencds.cqf.cql.engine.execution;

import java.util.List;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LetClauseOutsideQueryContextTest extends CqlExecutionTestBase {
    @Test
    public void testInterval() {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("First Position of list").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 1));

        result = context.resolveExpressionRef("Third Position of list With Same Name of Let As First").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 3));
    }
}