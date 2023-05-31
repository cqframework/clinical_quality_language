package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;


public class Issue208 extends CqlTestBase {

    @Test
    public void testInterval() {

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("Issue208"));
        Object result = evaluationResult.expressionResults.get("Let Test 1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(0), 1));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(1), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(2), 3));

        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(0), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(1), 5));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(2), 6));

        result = evaluationResult.expressionResults.get("Let Test 2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(0), 1));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(1), 2));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(0))).get(2), 3));

        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(0), 4));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(1), 5));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)(((List<?>) result).get(1))).get(2), 6));
    }
}
