package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import org.testng.annotations.Test;

public class IncludedSignatureWithAliasOutputTests extends CqlTestBase {

    @Test
    public void testEvaluate() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("IncludedSignatureWithAliasOutputTests"));

        Object result = evaluationResult.forExpression("TestOverload").value();
        assertThat(result, is(5));

        result = evaluationResult.forExpression("TestOverloadOneInt").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TestOverloadOneDecimal").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("TestOverloadTwoInts").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("TestOverloadTwoDecimals").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("TestOverloadOneIntOneDecimal").value();
        assertThat(result, is(new BigDecimal("2.0")));

        result = evaluationResult.forExpression("TestOverloadOneIntTwoDecimal").value();
        assertThat(result, is(1));
    }
}
