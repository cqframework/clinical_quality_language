package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SignatureOutputTests extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("SignatureOutputTests"));


        Object result = evaluationResult.expressionResults.get("TestIntegerOverload").value();
        assertThat(result, is(new BigDecimal("1")));

        result = evaluationResult.expressionResults.get("TestDecimalOverload").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("TestMultipleOverload").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TestIntegerMultipleOverload").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestDecimalMultipleOverload").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("TestIntegerAndDecimalMultipleOverload").value();
        assertThat(result, is(1));
    }
}
