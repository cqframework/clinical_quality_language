package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.Test;

public class Issue39 extends CqlTestBase {

    @Test
    public void testInterval() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("Issue39"));
        Object result = evaluationResult.forExpression("EquivalentIntervals").value();
        assertThat(result, is(true));
    }
}
