package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlFunctionOverloadTest extends CqlTestBase {

    @Test
    public void test_function_overloads() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("FunctionOverloadTest"), null, null, null, null, null);

        Object result;

        result = evaluationResult.expressionResults.get("TestAnyFunctionWithInteger").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestAnyFunctionWithString").value();
        assertThat(result, is("joe"));

        result = evaluationResult.expressionResults.get("TestAnyFunctionWithDecimal").value();
        assertThat(result, is(new BigDecimal("12.3")));

        result = evaluationResult.expressionResults.get("TestAnyFunctionWithNoArgs").value();
        assertThat(result, is("any"));

    }
}
