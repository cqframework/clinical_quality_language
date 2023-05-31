package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;


public class CqlQueryTest extends CqlTestBase {

    @Test
    public void test_all_query_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlQueryTests"));
        Object result;

        result = evaluationResult.expressionResults.get("RightShift").value();
        Assert.assertEquals(result, Arrays.asList(null, "A", "B", "C"));
        result = evaluationResult.expressionResults.get("LeftShift").value();
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));
        result = evaluationResult.expressionResults.get("LeftShift2").value();
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));

        result = evaluationResult.expressionResults.get("Multisource").value();
        Assert.assertTrue(result instanceof List);
        List<?> results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0) instanceof Tuple);
        Tuple resultTuple = (Tuple) results.get(0);
        Assert.assertTrue(resultTuple.getElements().containsKey("A") && resultTuple.getElements().containsKey("B"));

        result = evaluationResult.expressionResults.get("Complex Multisource").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 4);

        result = evaluationResult.expressionResults.get("Let Test Fails").value();


        result = evaluationResult.expressionResults.get("Triple Source Query").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 27);

        result = evaluationResult.expressionResults.get("Let Expression in Multi Source Query").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));


        result = evaluationResult.expressionResults.get("Accessing Third Element of Triple Source Query").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));


    }
}
