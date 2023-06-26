package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EmptyStringsTest extends CqlTestBase {

    @Test
    public void test_all_empty_string() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("EmptyStringsTest"));

        Object result = evaluationResult.expressionResults.get("Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Space").value();
        assertThat(result, is(" "));

        result = evaluationResult.expressionResults.get("Empty").value();
        assertThat(result, is(""));


    }
}
