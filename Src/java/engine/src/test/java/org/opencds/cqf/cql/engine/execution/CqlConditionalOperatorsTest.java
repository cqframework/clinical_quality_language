package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlConditionalOperatorsTest extends CqlTestBase {
    @Test
    public void test_all_conditional_operators_tests() throws IOException {

        EvaluationResult evaluationResult;
        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlConditionalOperatorsTest"));

        Object result = result = evaluationResult.expressionResults.get("IfTrue1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.expressionResults.get("IfFalse1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.expressionResults.get("IfNull1").value();
        assertThat(result, is(10));
        result = result = evaluationResult.expressionResults.get("StandardCase1").value();
        assertThat(result, is(5));

        result = result = evaluationResult.expressionResults.get("StandardCase2").value();
        assertThat(result, is(5));

        result = result = evaluationResult.expressionResults.get("StandardCase3").value();
        assertThat(result, is(15));
        
    }
}
