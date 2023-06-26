package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CqlFunctionTest extends CqlTestBase {

    @Test
    public void test_all_function_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlFunctionTests"));

        Object result;
        result = evaluationResult.expressionResults.get("FunctionTestStringArg").value();
        assertThat(result, is("hello"));

        result = evaluationResult.expressionResults.get("FunctionTestNullStringArg").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FunctionTestMultipleArgs").value();
        assertThat(result, is("hell0"));

        result = evaluationResult.expressionResults.get("FunctionTestNullMultipleArgs").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FunctionTestOverload").value();
        assertThat(result, is("hell00.000"));

        result = evaluationResult.expressionResults.get("FunctionTestNullOverload").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FunctionTestTupleArg").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("FunctionTestNullTupleArg").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FunctionTestQuantityArg").value();
        assertThat(result, is("cm"));

        result = evaluationResult.expressionResults.get("FunctionTestNullQuantityArg").value();
        assertThat(result, is(nullValue()));

    }
}
