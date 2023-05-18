package org.opencds.cqf.cql.engine.execution;

import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlExternalFunctionsTest extends CqlTestBase {

    @Test
    public void testExternalFunctions() {
        VersionedIdentifier identifier = toElmIdentifier("CqlExternalFunctionsTest");

        engineVisitor.getState().registerExternalFunctionProvider(
                identifier,
                new SystemExternalFunctionProvider(Arrays.asList(MyMath.class.getDeclaredMethods()))
        );

        EvaluationResult evaluationResult = engineVisitor.evaluate(identifier, null, null, null, null, null);

        Object result;

        result = evaluationResult.expressionResults.get("CallMyPlus").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("CallMyMinus").value();
        assertThat(result, is(-2));
    }
}
