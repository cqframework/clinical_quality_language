package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlConditionalOperatorsTest extends CqlTestBase {
    @Test
    public void test_all_conditional_operators_tests() throws IOException {

        EvaluationResult evaluationResult;
        evaluationResult = engine.evaluate(toElmIdentifier("CqlConditionalOperatorsTest"));

        Object result = result = evaluationResult.forExpression("IfTrue1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.forExpression("IfFalse1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.forExpression("IfNull1").value();
        assertThat(result, is(10));
        result = result = evaluationResult.forExpression("StandardCase1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.forExpression("StandardCase2").value();
        assertThat(result, is(5));

        result = result = evaluationResult.forExpression("StandardCase3").value();
        assertThat(result, is(15));

    }
}
