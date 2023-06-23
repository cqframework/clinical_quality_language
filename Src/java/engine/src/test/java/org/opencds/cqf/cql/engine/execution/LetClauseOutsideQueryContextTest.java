package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class LetClauseOutsideQueryContextTest extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("LetClauseOutsideQueryContextTest"));


        Object result = evaluationResult.expressionResults.get("First Position of list").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 1));

        result = evaluationResult.expressionResults.get("Third Position of list With Same Name of Let As First").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), 3));
    }
}
