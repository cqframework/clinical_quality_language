package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

public class CqlFunctionTest extends CqlTestBase {

    @Test
    public void test_all_function_tests() {
        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toIdentifier("CqlFunctionTests"));

        Object result;
        result = evaluationResult.forExpression("FunctionTestStringArg").value();
        assertThat(result, is("hello"));

        result = evaluationResult.forExpression("FunctionTestNullStringArg").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FunctionTestMultipleArgs").value();
        assertThat(result, is("hell0"));

        result = evaluationResult.forExpression("FunctionTestNullMultipleArgs").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FunctionTestOverload").value();
        assertThat(result, is("hell00.000"));

        result = evaluationResult.forExpression("FunctionTestNullOverload").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FunctionTestTupleArg").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("FunctionTestNullTupleArg").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FunctionTestQuantityArg").value();
        assertThat(result, is("cm"));

        result = evaluationResult.forExpression("FunctionTestNullQuantityArg").value();
        assertThat(result, is(nullValue()));
    }
}
