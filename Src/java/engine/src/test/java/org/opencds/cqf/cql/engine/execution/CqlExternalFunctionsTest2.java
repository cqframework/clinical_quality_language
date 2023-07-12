package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath2;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlExternalFunctionsTest2 extends CqlTestBase {

    @Test
    public void testExternalFunctions() {
        VersionedIdentifier identifier = toElmIdentifier("CqlExternalFunctionsTest2");

         engineVisitor.getState().getEnvironment().registerExternalFunctionProvider(
                identifier,
                new SystemExternalFunctionProvider(Arrays.asList(MyMath2.class.getDeclaredMethods()))
        );

        EvaluationResult evaluationResult;
        evaluationResult = engineVisitor.evaluate(identifier);

        Object result;

        result = evaluationResult.expressionResults.get("CallMyTimes").value();
        assertThat(result, is(54));

        result = evaluationResult.expressionResults.get("CallMyDividedBy").value();
        assertThat(result, is(6));
    }
}
