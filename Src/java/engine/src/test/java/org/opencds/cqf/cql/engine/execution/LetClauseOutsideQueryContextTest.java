package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class LetClauseOutsideQueryContextTest extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("LetClauseOutsideQueryContextTest"));


        Object result = evaluationResult.forExpression("First Position of list").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 1));

        result = evaluationResult.forExpression("Third Position of list With Same Name of Let As First").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 3));
    }
}
