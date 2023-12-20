package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.testng.annotations.Test;

public class CqlFunctionOverloadTest extends CqlTestBase {

    @Test
    public void test_function_overloads() {
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("FunctionOverloadTest"));

        Object result;

        result = evaluationResult.forExpression("TestAnyFunctionWithInteger").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TestAnyFunctionWithString").value();
        assertThat(result, is("joe"));

        result = evaluationResult.forExpression("TestAnyFunctionWithDecimal").value();
        assertThat(result, is(new BigDecimal("12.3")));

        result = evaluationResult.forExpression("TestAnyFunctionWithNoArgs").value();
        assertThat(result, is("any"));
    }
}
