package org.opencds.cqf.cql.engine.execution;

import java.util.Arrays;
import java.util.List;

import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlQueryTests extends CqlExecutionTestBase
{
    @Test
    public void TestLet()
    {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("RightShift").getExpression().evaluate(context);
        Assert.assertEquals(result, Arrays.asList(null, "A", "B", "C"));
        result = context.resolveExpressionRef("LeftShift").getExpression().evaluate(context);
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));
        result = context.resolveExpressionRef("LeftShift2").getExpression().evaluate(context);
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));

        result = context.resolveExpressionRef("Multisource").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List);
        List<?> results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0) instanceof Tuple);
        Tuple resultTuple = (Tuple) results.get(0);
        Assert.assertTrue(resultTuple.getElements().containsKey("A") && resultTuple.getElements().containsKey("B"));

        result = context.resolveExpressionRef("Complex Multisource").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 4);

        result = context.resolveExpressionRef("Let Test Fails").getExpression().evaluate(context);


        result = context.resolveExpressionRef("Triple Source Query").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 27);

        result = context.resolveExpressionRef("Let Expression in Multi Source Query").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));


        result = context.resolveExpressionRef("Accessing Third Element of Triple Source Query").getExpression().evaluate(context);
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));
    }

    @Test
    public void TestWithout()
    {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ClaimWithQualifiyingPOSWithoutEncounter").getExpression().evaluate(context);
        Assert.assertTrue(result == null);
    }
}
