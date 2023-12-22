package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.testng.annotations.Test;

public class SignatureOutputTests extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("SignatureOutputTests"));

        Object result = evaluationResult.forExpression("TestIntegerOverload").value();
        assertThat(result, is(new BigDecimal("1")));

        result = evaluationResult.forExpression("TestDecimalOverload").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.forExpression("TestMultipleOverload").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("TestIntegerMultipleOverload").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TestDecimalMultipleOverload").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult
                .forExpression("TestIntegerAndDecimalMultipleOverload")
                .value();
        assertThat(result, is(1));
    }
}
