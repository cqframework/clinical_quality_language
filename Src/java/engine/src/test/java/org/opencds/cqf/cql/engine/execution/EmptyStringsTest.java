package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EmptyStringsTest extends CqlTestBase {

    @Test
    public void test_all_empty_string() {
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("EmptyStringsTest"));

        Object result = evaluationResult.forExpression("Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Space").value();
        assertThat(result, is(" "));

        result = evaluationResult.forExpression("Empty").value();
        assertThat(result, is(""));


    }
}
