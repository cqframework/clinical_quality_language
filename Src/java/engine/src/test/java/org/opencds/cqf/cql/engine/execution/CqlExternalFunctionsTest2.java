package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider;
import org.opencds.cqf.cql.engine.execution.external.MyMath2;
import org.testng.annotations.Test;

public class CqlExternalFunctionsTest2 extends CqlTestBase {

    @Test
    public void testExternalFunctions() {
        VersionedIdentifier identifier = toElmIdentifier("CqlExternalFunctionsTest2");

        engine.getState()
                .getEnvironment()
                .registerExternalFunctionProvider(
                        identifier,
                        new SystemExternalFunctionProvider(Arrays.asList(MyMath2.class.getDeclaredMethods())));

        EvaluationResult evaluationResult;
        evaluationResult = engine.evaluate(identifier);

        Object result;

        result = evaluationResult.forExpression("CallMyTimes").value();
        assertThat(result, is(54));

        result = evaluationResult.forExpression("CallMyDividedBy").value();
        assertThat(result, is(6));
    }
}
