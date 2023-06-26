package org.opencds.cqf.cql.engine.execution;

import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IncludedSignatureWithAliasOutputTests extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("IncludedSignatureWithAliasOutputTests"));
        
        Object result = evaluationResult.expressionResults.get("TestOverload").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TestOverloadOneInt").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestOverloadOneDecimal").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("TestOverloadTwoInts").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestOverloadTwoDecimals").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("TestOverloadOneIntOneDecimal").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.expressionResults.get("TestOverloadOneIntTwoDecimal").value();
        assertThat(result, is(1));
    }
}
