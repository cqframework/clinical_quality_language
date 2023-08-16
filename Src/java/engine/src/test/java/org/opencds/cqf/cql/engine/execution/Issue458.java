package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class Issue458 extends CqlTestBase {

    @Test
    public void testInterval() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("Issue458"));
        Object result = evaluationResult.expressionResults.get("Closed-Open Interval").value();
        Interval interval = (Interval)result;
        assertEquals(interval.toString(), "Interval[3, 5)");

    }
}
