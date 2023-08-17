package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class SortDescendingTest extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("SortDescendingTest"));


        Object result = evaluationResult.forExpression("sorted list of numbers descending").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(0), 9));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(1), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(2), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(3), 1));
    }
}
