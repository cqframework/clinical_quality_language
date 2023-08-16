package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.Code;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertNotNull;


public class IncludedCodeRefTest extends CqlTestBase {

    @Test
    public void test_included_code_ref() {

        EvaluationResult evaluationResult;

        evaluationResult = engine.evaluate(toElmIdentifier("IncludedCodeRefTest"));
        Object result = evaluationResult.forExpression("IncludedCode").value();
        assertNotNull(result);
        assertThat(result, is(instanceOf(Code.class)));

    }
}
