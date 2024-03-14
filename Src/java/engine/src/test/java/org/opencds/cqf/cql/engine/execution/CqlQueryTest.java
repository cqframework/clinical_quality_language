package org.opencds.cqf.cql.engine.execution;

import java.util.*;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CqlQueryTest extends CqlTestBase {

    @Test
    public void test_all_query_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("CqlQueryTests"));
        Object result;

        result = evaluationResult.forExpression("RightShift").value();
        Assert.assertEquals(result, Arrays.asList(null, "A", "B", "C"));
        result = evaluationResult.forExpression("LeftShift").value();
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));
        result = evaluationResult.forExpression("LeftShift2").value();
        Assert.assertEquals(result, Arrays.asList("B", "C", "D", null));

        result = evaluationResult.forExpression("Multisource").value();
        Assert.assertTrue(result instanceof List);
        List<?> results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0) instanceof Tuple);
        Tuple resultTuple = (Tuple) results.get(0);
        Assert.assertTrue(resultTuple.getElements().containsKey("A")
                && resultTuple.getElements().containsKey("B"));

        result = evaluationResult.forExpression("Complex Multisource").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 4);

        result = evaluationResult.forExpression("Let Test Fails").value();

        result = evaluationResult.forExpression("Triple Source Query").value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 27);

        result = evaluationResult
                .forExpression("Let Expression in Multi Source Query")
                .value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));

        result = evaluationResult
                .forExpression("Accessing Third Element of Triple Source Query")
                .value();
        Assert.assertTrue(result instanceof List);
        results = (List<?>) result;
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(EquivalentEvaluator.equivalent(results.get(0), 3));
    }
}
